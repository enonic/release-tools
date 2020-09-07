package com.enonic.xp.changelog.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.changelog.git.model.GitCommit;
import com.enonic.xp.changelog.github.model.GitHubIssue;
import com.enonic.xp.changelog.github.model.GitHubIssueIdComparator;
import com.enonic.xp.changelog.zenhub.ZenHubHelper;

public class GitHubServiceImpl
    implements GitHubService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GitHubServiceImpl.class );

    private ArrayList<String> ignoreLabels = new ArrayList<>();

    private List<GitHubIssue> noLabel;

    private HashMap<String, List<GitHubIssue>> issues;

    private List<Integer> issuesInEpics;

    private final GHRepository repo;

    public GitHubServiceImpl( final String repository )
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

    @Override
    public HashMap<String, List<GitHubIssue>> retrieveGitHubIssues( final Set<GitCommit> issueNumbers )
        throws IOException
    {
        LOGGER.info( "Retrieving GitHub issues with GitHub issue IDs..." );

        Set<GitCommit> updatedIssueNumbers = appendEpicsWithoutCommits( issueNumbers );

        issues = new HashMap<>( updatedIssueNumbers.size() );
        noLabel = new ArrayList<>();

        for ( GitCommit commit : updatedIssueNumbers )
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
        filterBugsInEpics();
        listIssuesWithoutLabelsInLog();
        return issues;
    }

    private Set<GitCommit> appendEpicsWithoutCommits( final Set<GitCommit> issueNumbers )
        throws IOException
    {
        HashSet<Integer> issueNos =
            issueNumbers.stream().mapToInt( GitCommit::getGitHubIdAsInt ).boxed().collect( Collectors.toCollection( HashSet::new ) );

        HashMap<Integer, Integer> issuesWithEpics =
            ZenHubHelper.getAllIssuesInEpicsWithEpic( getRepoId(), System.getenv( "ZENHUB_TOKEN" ) );

        HashSet<Integer> issuesInCommitsAndEpics =
            issueNos.stream().filter( issueNo -> issuesWithEpics.keySet().contains( issueNo ) ).collect(
                Collectors.toCollection( HashSet::new ) );

        LOGGER.debug( "Issues in Epics:" );
        issuesInCommitsAndEpics.stream().forEach(
            issueNo -> LOGGER.debug( issueNo.toString() + " - " + issuesWithEpics.get( issueNo ).toString() ) );

        HashSet<Integer> epicsOfCommits = new HashSet<>();
        for ( Integer issueInCommitAndEpic : issuesInCommitsAndEpics )
        {
            epicsOfCommits.add( issuesWithEpics.get( issueInCommitAndEpic ) );
        }

        HashSet<GitCommit> missingEpics = new HashSet<>();
        for ( Integer epicOfCommit : epicsOfCommits )
        {
            if ( !issueNos.contains( epicOfCommit ) )
            {
                missingEpics.add( new GitCommit( epicOfCommit, "" ) );
            }

        }

        HashSet<GitCommit> result = new HashSet<>();
        result.addAll( issueNumbers );
        result.addAll( missingEpics );
        return result;
    }

    private void listIssuesWithoutLabelsInLog()
        throws IOException
    {
        filterNoLabelsInEpics();
        noLabel.sort( new GitHubIssueIdComparator<GitHubIssue>() );
        for ( GitHubIssue noLabelIssue : noLabel )
        {
            LOGGER.debug( "No label: #" + noLabelIssue.getGitHubIssueId() + " - " + noLabelIssue.getTitle() );
        }
    }

    private void filterNoLabelsInEpics()
        throws IOException
    {
        List<Integer> issuesInEpics = getAllIssuesInEpics();
        if ( issuesInEpics == null )
        {
            return;
        }
        final List<GitHubIssue> issuesWithoutLable = new ArrayList<>();
        issuesWithoutLable.addAll( noLabel );
        for ( GitHubIssue issueWithoutLabel : issuesWithoutLable )
        {
            if ( issuesInEpics.contains( issueWithoutLabel.getGitHubIssueId() ) )
            {
                noLabel.remove( issueWithoutLabel );
            }
        }
    }

    private void filterBugsInEpics()
        throws IOException
    {
        List<Integer> allIssuesInEpics = getAllIssuesInEpics();
        if ( allIssuesInEpics == null )
        {
            return;
        }
        if ( issues.get( "Bug" ) != null )  // Small releases may not have any bugs at all
        {
            final List<GitHubIssue> bugs = new ArrayList<>();
            bugs.addAll( issues.get( "Bug" ) );
            for ( GitHubIssue bug : bugs )
            {
                if ( allIssuesInEpics.contains( bug.getGitHubIssueId() ) )
                {
                    issues.get( "Bug" ).remove( bug );
                    LOGGER.debug( "Removed bug #" + bug.getGitHubIssueId() + " from changelog, because it is a child of an Epic." );
                }
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
        LOGGER.debug( i.getNumber() + " : " + i.toString() );
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

    private List<Integer> getAllIssuesInEpics()
        throws IOException
    {
        if ( issuesInEpics == null )
        {
            issuesInEpics = ZenHubHelper.getAllIssuesInAllEpics( getRepoId(), System.getenv( "ZENHUB_TOKEN" ) );
        }
        return issuesInEpics;
    }

    @Override
    public void addIgnoreLabel( final String label )
    {
        ignoreLabels.add( label );
    }

    @Override
    public long getRepoId()
    {
        return repo.getId();
    }

    @Override
    public String getProjectName()
    {
        return repo.getName();
    }
}
