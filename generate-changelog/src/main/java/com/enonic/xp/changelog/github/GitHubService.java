package com.enonic.xp.changelog.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.changelog.git.model.GitCommit;
import com.enonic.xp.changelog.github.model.GitHubIssue;

public class GitHubService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GitHubService.class );

    private final ArrayList<String> ignoreLabels = new ArrayList<>();

    private List<GitHubIssue> noLabel;

    private Map<String, List<GitHubIssue>> issues;

    private final GHRepository repo;

    public GitHubService( final String repository )
        throws IOException
    {
        final GitHub gitHub;
        if ( System.getenv( "GITHUB_ACTOR" ) != null )
        {
            gitHub = GitHub.connect( System.getenv( "GITHUB_ACTOR" ), System.getenv( "GITHUB_TOKEN" ) );
        }
        else
        {
            gitHub = GitHub.connectUsingOAuth( System.getenv( "GITHUB_TOKEN" ) );
        }
        repo = gitHub.getRepository( repository );
    }

    public Map<String, List<GitHubIssue>> retrieveGitHubIssues( final Collection<GitCommit> issueNumbers )
        throws IOException
    {
        LOGGER.info( "Retrieving GitHub issues with GitHub issue IDs..." );

        issues = new HashMap<>();
        noLabel = new ArrayList<>();

        for ( GitCommit commit : issueNumbers )
        {
            try
            {
                GHIssue i = repo.getIssue( commit.getGitHubIdAsInt() );
                verifyAndAddIssue( i );
            }
            catch ( IOException e )
            {
                Throwable parent = e.getCause();
                LOGGER.warn( "WARNING: Issue #" + commit.getGitHubIdAsString() + " can not be found: " + e.getMessage() + " - Caused by: " +
                                 parent.getMessage() );
            }
        }
        listIssuesWithoutLabelsInLog();
        return issues;
    }

    private void listIssuesWithoutLabelsInLog()
    {
        noLabel.sort( Comparator.comparingInt( GitHubIssue::getGitHubIssueId ) );
        for ( GitHubIssue noLabelIssue : noLabel )
        {
            LOGGER.debug( "No label: #" + noLabelIssue.getGitHubIssueId() + " - " + noLabelIssue.getTitle() );
        }
    }

    private void verifyAndAddIssue( final GHIssue i )
        throws IOException
    {
        if ( i.isPullRequest() )
        {
            return;
        }
        LOGGER.debug( i.getNumber() + " : " + i );
        if ( i.getLabels().size() < 1 )
        {
            noLabel.add( new GitHubIssue( i.getNumber(), i.getTitle() ) );
            return;
        }
        for ( String ignoreLabel : ignoreLabels )
        {
            for ( GHLabel label : i.getLabels() )
            {
                if ( ignoreLabel.equals( label.getName() ) )
                {
                    LOGGER.debug( " -> Ignored because of label: " + label.getName() );
                    return;
                }
            }
        }
        for ( GHLabel label : i.getLabels() )
        {
            String labelName = label.getName();
            if ( "bug".equals( labelName ) )
            {
                labelName = "Bug";
            }
            else if ( "enhancement".equals( labelName ) )
            {
                labelName = "Improvement";
            }
            List<GitHubIssue> list = issues.computeIfAbsent( labelName, k -> new ArrayList<>() );
            list.add( new GitHubIssue( i.getNumber(), i.getTitle() ) );
            LOGGER.debug( "  - " + label.getName() );
        }
    }

    public void addIgnoreLabel( final String label )
    {
        ignoreLabels.add( label );
    }

    public String getProjectName()
    {
        return repo.getName();
    }
}
