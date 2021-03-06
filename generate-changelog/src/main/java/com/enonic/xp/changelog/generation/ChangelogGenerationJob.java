package com.enonic.xp.changelog.generation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.CharSink;
import com.google.common.io.Files;

import com.enonic.xp.changelog.github.model.GitHubIssue;

public class ChangelogGenerationJob
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ChangelogGenerationJob.class );

    private HashMap<String, List<GitHubIssue>> gitHubIssueCollection;

    private List<String> labelOrder = new ArrayList<>();

    private final String since;

    private final String until;

    private final String projectName;

    private final String filename;

    private int gitHubIssuesGenerated = 0;

    private StringBuilder changeLogContent = new StringBuilder( "# Changelog" ).append( System.lineSeparator() );

    public ChangelogGenerationJob( final HashMap<String, List<GitHubIssue>> gitHubIssueCollection, final String since, final String until,
                                   final String projectName, final String filename )
    {
        this.gitHubIssueCollection = gitHubIssueCollection;
        this.since = since;
        this.until = until;
        this.projectName = projectName;
        this.filename = filename;
        defineLabelOrder();
    }

    private void defineLabelOrder()
    {
//        if ( gitHubIssueCollection.containsKey( "Epic" ) )
//        {
//            labelOrder.add( "Epic" );
//        }
        if ( gitHubIssueCollection.containsKey( "Feature" ) )
        {
            labelOrder.add( "Feature" );
        }
        if ( gitHubIssueCollection.containsKey( "Improvement" ) )
        {
            labelOrder.add( "Improvement" );
        }
        if ( gitHubIssueCollection.containsKey( "Bug" ) )
        {
            labelOrder.add( "Bug" );
        }
        if ( gitHubIssueCollection.containsKey( "Refactoring" ) )
        {
            labelOrder.add( "Refactoring" );
        }
    }


    public void run()
        throws IOException
    {
        LOGGER.info( "Writing changelog..." );
        //Generates the content
        generateChangelogContent();

        //Writes the content in the output MD file
        generateChangelogFile();
        LOGGER.info( gitHubIssuesGenerated + " GitHub issues written in the changelog." );
    }

    private void generateChangelogContent()
    {
        int issueCount = 0;
        for ( String label : labelOrder )
        {
            changeLogContent.append( System.lineSeparator() ).append( "## " ).append( label ).append( "s" ).append(
                System.lineSeparator() );
            List<GitHubIssue> sectionIssues = gitHubIssueCollection.get( label );
            sectionIssues.sort( GitHubIssue::compareTo );
            sectionIssues.forEach( issue -> generateChangelogContent( issue, 0 ) );
            issueCount += sectionIssues.size();
        }

        if ( issueCount == 0 )
        {
            changeLogContent.append( System.lineSeparator() ).append( "There have been no changes to the project " );
            if ( !Strings.isNullOrEmpty( since ) && !Strings.isNullOrEmpty( until ) )
            {
                changeLogContent.append( "between " ).append( since ).append( " and " ).append( until ).append( "." );
            }
            else if ( Strings.isNullOrEmpty( since ) && !Strings.isNullOrEmpty( until ) )
            {
                changeLogContent.append( "before " ).append( until ).append( "." );
            }
            else if ( !Strings.isNullOrEmpty( since ) && Strings.isNullOrEmpty( until ) )
            {
                changeLogContent.append( "after " ).append( since ).append( "." );
            }
        }
        changeLogContent.append( System.lineSeparator() );
        LOGGER.debug( "Changelog content: " );
        LOGGER.debug( changeLogContent.toString() );
    }

    private void generateChangelogContent( final GitHubIssue issue, final int depth )
    {
        gitHubIssuesGenerated++;

        for ( int i = 0; i < depth; i++ )
        {
            changeLogContent.append( "  " );
        }

        changeLogContent.append( " - " ).
            append( issue.getTitle() ).
            append( " (#" ).
            append( issue.getGitHubIssueId() );

        changeLogContent.append( ")." ).append( System.lineSeparator() );

    }

    private void generateChangelogFile()
        throws IOException
    {
        //Creates the output file
        final String fileName = Optional.ofNullable( filename ).orElseGet( this::generateFileName );

        final File file = new File( fileName );

        //Writes the output file content
        final CharSink charSink = Files.asCharSink( file, Charset.forName( "UTF-8" ) );
        charSink.write( changeLogContent.toString() );
    }

    private String generateFileName()
    {
        StringBuilder changelogFileName = new StringBuilder( "changelog_" );
        changelogFileName.append( projectName );

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
