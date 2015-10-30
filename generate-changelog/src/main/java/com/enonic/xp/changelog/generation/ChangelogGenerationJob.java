package com.enonic.xp.changelog.generation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharSink;
import com.google.common.io.Files;

import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;

public class ChangelogGenerationJob
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ChangelogGenerationJob.class );

    private Collection<YouTrackIssue> youTrackIssueCollection;

    private final String since;

    private final String until;

    private final Predicate<YouTrackIssue> filter;

    private int youTrackIssueGenerated = 0;

    private StringBuilder changeLogContent = new StringBuilder( "# Changelog\n" );

    public ChangelogGenerationJob( final Collection<YouTrackIssue> youTrackIssueCollection, final String since, final String until,
                                   final Predicate<YouTrackIssue> filter )
    {
        this.youTrackIssueCollection = youTrackIssueCollection;
        this.since = since;
        this.until = until;
        this.filter = filter;
    }

    public void run()
        throws IOException
    {
        LOGGER.info( "Writing changelog..." );
        //Generates the content
        generateChangelogContent();

        //Writes the content in the output MD file
        generateChangelogFile();
        LOGGER.info( youTrackIssueGenerated + " YouTrack issues written in the changelog." );
    }

    private void generateChangelogContent()
    {
        //Retrieves the filtered root YouTrackIssues and group them by category
        final Map<String, List<YouTrackIssue>> youTrackIssueByType = youTrackIssueCollection.stream().
            map( youTrackIssue -> findRootYouTrackIssue( youTrackIssue ) ).
            distinct().
            filter( filter ).
            collect( Collectors.groupingBy( youTrackIssue1 -> youTrackIssue1.getType() ) );

        //Sorts by type and for each type
        youTrackIssueByType.entrySet().
            stream().
            sorted( ( entry1, entry2 ) -> entry1.getKey().compareTo( entry2.getKey() ) ).
            forEach( youTrackIssueEntry -> {
                //Writes the category title
                changeLogContent.append( "\n## " ).append( youTrackIssueEntry.getKey() ).append( "s\n" );

                //Calls recursively the writing of the root YouTrackIssues and their filtered children
                youTrackIssueEntry.getValue().
                    forEach( youTrackIssue -> generateChangelogContent( youTrackIssue, 0 ) );
            } );

        LOGGER.debug( "Changelog content: " + changeLogContent );
    }

    private YouTrackIssue findRootYouTrackIssue( final YouTrackIssue youTrackIssue )
    {
        final YouTrackIssue parentYouTrackIssue = youTrackIssue.getParent();
        if ( parentYouTrackIssue == null || parentYouTrackIssue.isEpic() )
        {
            return youTrackIssue;
        }
        else
        {
            return findRootYouTrackIssue( parentYouTrackIssue );
        }
    }

    private void generateChangelogContent( final YouTrackIssue youTrackIssue, final int depth )
    {
        youTrackIssueGenerated++;

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
        changeLogContent.append( ")." ).
            append( System.lineSeparator() );

        youTrackIssue.getChildren().
            stream().
            filter( filter ).
            forEach( childYouTrackIssue -> generateChangelogContent( childYouTrackIssue, depth + 1 ) );
    }

    private void generateChangelogFile()
        throws IOException
    {
        //Creates the output file
        final String fileName = generateFileName();
        final File file = new File( fileName );

        //Writes the output file content
        final CharSink charSink = Files.asCharSink( file, Charset.forName( "UTF-8" ) );
        charSink.write( changeLogContent.toString() );
    }

    private String generateFileName()
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
