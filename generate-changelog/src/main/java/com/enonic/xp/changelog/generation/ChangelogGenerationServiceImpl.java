package com.enonic.xp.changelog.generation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharSink;
import com.google.common.io.Files;

import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;

public class ChangelogGenerationServiceImpl
    implements ChangelogGenerationService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ChangelogGenerationServiceImpl.class );

    @Override
    public void generateChangelog( final Collection<YouTrackIssue> youTrackIssueCollection, final String since, final String until )
        throws IOException
    {
        //Generates the content
        final String changelogContent = generateChangelogContent( youTrackIssueCollection );

        //Writes the content in the output MD file
        generateChangelogFile( changelogContent, since, until );
    }

    private String generateChangelogContent( final Collection<YouTrackIssue> youTrackIssueCollection )
    {
        StringBuilder changeLogContent = new StringBuilder( "# Changelog\n" );

        //Retrieves the root YouTrackIssues and group them by category
        final Map<String, List<YouTrackIssue>> youTrackIssueByType = youTrackIssueCollection.stream().
            map( youTrackIssue -> findRootYouTrackIssue( youTrackIssue ) ).
            distinct().
            collect( Collectors.groupingBy( youTrackIssue1 -> youTrackIssue1.getField( YouTrackIssue.TYPE_FIELD_NAME ).toString() ) );

        //Sorts by category and for each category
        youTrackIssueByType.entrySet().
            stream().
            sorted( ( entry1, entry2 ) -> entry1.getKey().compareTo( entry2.getKey() ) ).
            forEach( youTrackIssueEntry -> {
                //Writes the category title
                changeLogContent.append( "\n## " ).append( youTrackIssueEntry.getKey() ).append( "s\n" );

                //Calls recursively the writing of the root YouTrackIssues and their children
                youTrackIssueEntry.getValue().
                    stream().
                    forEach( youTrackIssue -> generateChangelogContent( changeLogContent, youTrackIssue, 0 ) );
            } );

        LOGGER.debug( "Changelog content: " + changeLogContent );
        return changeLogContent.toString();
    }

    private YouTrackIssue findRootYouTrackIssue( final YouTrackIssue youTrackIssue )
    {
        final YouTrackIssue parentYouTrackIssue = youTrackIssue.getParent();
        if ( parentYouTrackIssue == null )
        {
            return youTrackIssue;
        }
        else
        {
            return findRootYouTrackIssue( parentYouTrackIssue );
        }
    }

    private void generateChangelogContent( final StringBuilder changeLogContent, final YouTrackIssue youTrackIssue, final int depth )
    {
        for ( int i = 0; i < depth; i++ )
        {
            changeLogContent.append( "  " );
        }

        changeLogContent.append( " - " ).
            append( youTrackIssue.getField( YouTrackIssue.SUMMARY_FIELD_NAME ) ).
            append( " (" ).
            append( youTrackIssue.getId() );

        if ( depth > 0 )
        {
            changeLogContent.append( ", " ).
                append( youTrackIssue.getField( YouTrackIssue.TYPE_FIELD_NAME ) );
        }
        changeLogContent.append( ").\n" );

        youTrackIssue.getChildren().
            stream().
            forEach( childYouTrackIssue -> generateChangelogContent( changeLogContent, childYouTrackIssue, depth + 1 ) );
    }

    private void generateChangelogFile( final String changelogContent, final String since, final String until )
        throws IOException
    {
        //Creates the output file
        final String fileName = generateFileName( since, until );
        final File file = new File( fileName );

        //Writes the output file content
        final CharSink charSink = Files.asCharSink( file, Charset.forName( "UTF-8" ) );
        charSink.write( changelogContent );
    }

    private String generateFileName( final String since, final String until )
    {
        StringBuilder changelogFileName = new StringBuilder( "changelog" );

        if ( since != null || until != null )
        {
            changelogFileName.append( "-" );
            if ( since != null )
            {
                changelogFileName.append( since );
            }
            changelogFileName.append( ".." );
            if ( until != null )
            {
                changelogFileName.append( until );
            }
        }
        changelogFileName.append( ".md" );

        return changelogFileName.toString();
    }
}
