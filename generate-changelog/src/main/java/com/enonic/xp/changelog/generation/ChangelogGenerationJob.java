package com.enonic.xp.changelog.generation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.changelog.github.model.GitHubIssue;

public class ChangelogGenerationJob
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ChangelogGenerationJob.class );

    private final Map<String, List<GitHubIssue>> gitHubIssueCollection;

    private final List<String> labelOrder = new ArrayList<>();

    private final String since;

    private final String until;

    private final String projectName;

    private final String filename;

    private int gitHubIssuesGenerated = 0;

    private final StringBuilder changeLogContent = new StringBuilder( "# Changelog" ).append( System.lineSeparator() );

    public ChangelogGenerationJob( final Map<String, List<GitHubIssue>> gitHubIssueCollection, final String since, final String until,
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
        final String fileName = Optional.ofNullable( filename ).orElseGet( this::generateFileName );
        Files.writeString( Path.of( fileName ),changeLogContent.toString(), StandardCharsets.UTF_8 );
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
            sectionIssues.sort( Comparator.comparing( GitHubIssue::getTitle ) );
            sectionIssues.forEach( issue -> {
                gitHubIssuesGenerated++;

                changeLogContent.append( " - " ).
                    append( issue.getTitle() ).
                    append( " (#" ).
                    append( issue.getGitHubIssueId() ).append( ")." ).append( System.lineSeparator() );
            } );
            issueCount += sectionIssues.size();
        }

        if ( issueCount == 0 )
        {
            changeLogContent.append( System.lineSeparator() ).append( "There have been no changes to the project " );
            if ( !isNullOrEmpty( since ) && !isNullOrEmpty( until ) )
            {
                changeLogContent.append( "between " ).append( since ).append( " and " ).append( until ).append( "." );
            }
            else if ( isNullOrEmpty( since ) && !isNullOrEmpty( until ) )
            {
                changeLogContent.append( "before " ).append( until ).append( "." );
            }
            else if ( !isNullOrEmpty( since ) && isNullOrEmpty( until ) )
            {
                changeLogContent.append( "after " ).append( since ).append( "." );
            }
        }
        changeLogContent.append( System.lineSeparator() );
        LOGGER.debug( "Changelog content: " );
        LOGGER.debug( changeLogContent.toString() );
    }

    private static boolean isNullOrEmpty( final String string )
    {
        return string == null || string.isEmpty();
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
