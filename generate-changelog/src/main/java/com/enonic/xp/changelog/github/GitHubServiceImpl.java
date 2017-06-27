package com.enonic.xp.changelog.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.changelog.ChangelogException;
import com.enonic.xp.changelog.git.GitServiceHelper;
import com.enonic.xp.changelog.git.model.GitCommit;
import com.enonic.xp.changelog.github.model.GitHubIssue;
import com.enonic.xp.changelog.zenhub.ZenHubHelper;

public class GitHubServiceImpl
    implements GitHubService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GitHubServiceImpl.class );

    private ArrayList<String> ignoreLabels = new ArrayList<>();

    private List<GitHubIssue> noLabel;

    private HashMap<String, List<GitHubIssue>> issues;

    private Properties changelogProps;

    private String gitDirectoryPath;

    private GHRepository repo;

    public GitHubServiceImpl( final String gitDirectoryPath, final Properties changelogProperties )
        throws IOException, ChangelogException
    {
        this.gitDirectoryPath = gitDirectoryPath;
        this.changelogProps = changelogProperties;
        this.repo = getRepository();
    }

    private GHRepository getRepository()
        throws IOException, ChangelogException
    {
        final GitHub gitHub = GitHub.connect( changelogProps.getProperty( "user" ), changelogProps.getProperty( "oAuthToken" ) );
        return gitHub.getRepository( GitServiceHelper.findRepoName( gitDirectoryPath ) );
    }

    @Override
    public HashMap<String, List<GitHubIssue>> retrieveGitHubIssues( final Set<GitCommit> issueNumbers )
        throws IOException, ChangelogException
    {
        LOGGER.info( "Retrieving GitHub issues with GitHub issue IDs..." );

        issues = new HashMap<>( issueNumbers.size() );
        noLabel = new ArrayList<>();

        for ( GitCommit commit : issueNumbers )
        {
            GHIssue i = repo.getIssue( commit.getGitHubIdAsInt() );
            verifyAndAddIssue( i );
        }
        for ( GitHubIssue noLabelIssue : noLabel )
        {
            LOGGER.debug( "No label: #" + noLabelIssue.getGitHubIssueId() + " - " + noLabelIssue.getTitle() );
        }
        filterBugsInEpics();
        return issues;
    }

    private void filterBugsInEpics()
        throws IOException
    {
        final List<GitHubIssue> epics = issues.get( "Epic" );
        HashSet<Integer> bugsInEpics = new HashSet<>();
        for ( GitHubIssue epic : epics )
        {
            bugsInEpics.addAll( ZenHubHelper.getIssuesInEpic( epic.getGitHubIssueId(), getRepoId(), changelogProps.getProperty( "zenHubToken" ) ) );
        }
        final List<GitHubIssue> bugs = new ArrayList<>();
        bugs.addAll( issues.get( "Bug" ) );
        for ( GitHubIssue bug : bugs )
        {
            if ( bugsInEpics.contains( bug.getGitHubIssueId() ) )
            {
                issues.get( "Bug" ).remove( bug );
                LOGGER.debug( "Removed bug #" + bug.getGitHubIssueId() + " from changelog, because it is a child of an Epic." );
            }
        }
    }

    private void verifyAndAddIssue( final GHIssue i )
        throws IOException
    {
        if ( i.isPullRequest() )
        {
            return;
        }
        LOGGER.debug( i.toString() );
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
            List<GitHubIssue> list = issues.computeIfAbsent( label.getName(), k -> new ArrayList<>() );
            list.add( new GitHubIssue( i.getNumber(), i.getTitle() ) );
            LOGGER.debug( "  - " + label.getName() + " (" + label.getColor() + ")" );
        }
    }

    @Override
    public void addIgnoreLabel( final String label )
    {
        ignoreLabels.add( label );
    }

    @Override
    public Integer getRepoId()
    {
        return repo.getId();
    }

}
