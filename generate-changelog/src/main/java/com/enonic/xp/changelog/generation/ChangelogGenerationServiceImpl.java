package com.enonic.xp.changelog.generation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.common.io.CharSink;
import com.google.common.io.Files;

import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;

public class ChangelogGenerationServiceImpl
    implements ChangelogGenerationService
{
    private static final Logger LOGGER = LogManager.getLogger( ChangelogGenerationServiceImpl.class );

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
        StringBuilder changeLogContent = new StringBuilder( "# Changelog\n\n" );

        youTrackIssueCollection.stream().
            map( youTrackIssue -> findRootYouTrackIssue( youTrackIssue ) ).
            distinct().
            forEach( youTrackIssue -> generateChangelogContent( changeLogContent, youTrackIssue, "" ) );

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

    private void generateChangelogContent( final StringBuilder changeLogContent, final YouTrackIssue youTrackIssue, final String tab )
    {
        changeLogContent.append( tab ).
            append( "- " ).
            append( youTrackIssue.getField( YouTrackIssue.TYPE_FIELD_NAME ) ).
            append( " - " ).
            append( youTrackIssue.getField( YouTrackIssue.SUMMARY_FIELD_NAME ) ).
            append( " (" ).
            append( youTrackIssue.getId() ).
            append( ").\n" );

        youTrackIssue.getChildren().
            stream().
            forEach( childYouTrackIssue -> generateChangelogContent( changeLogContent, childYouTrackIssue, tab + "  " ) );
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
