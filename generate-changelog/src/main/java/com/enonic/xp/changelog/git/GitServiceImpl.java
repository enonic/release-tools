package com.enonic.xp.changelog.git;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.changelog.ChangelogException;
import com.enonic.xp.changelog.git.model.GitCommit;

public class GitServiceImpl
    implements GitService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GitServiceImpl.class );

    private static final Pattern GITHUB_ISSUE_ID_PATTERN = Pattern.compile( "(#[0-9]+)" );

    @Override
    public SortedSet<GitCommit> retrieveGitCommits( final String gitDirectoryPath, final String since, final String until )
        throws IOException, GitAPIException, ChangelogException
    {
        LOGGER.info( "Retrieving Git commits with GitHub issue IDs..." );

        //Retrieves the Git repository
        final Repository gitRepository = GitServiceHelper.retrieveGitRepository( gitDirectoryPath );

        //Retrieves the Git commits
        final Iterable<RevCommit> revCommitIterable = retrieveGitRevCommits( gitRepository, since, until );

        //Parses the Git commits
        final SortedSet<GitCommit> gitHubIssueCommits = retrieveGitHubIssueCommits( revCommitIterable );

        LOGGER.info( gitHubIssueCommits.size() + " Git commits with GitHub Issue IDs retrieved." );
        return gitHubIssueCommits;
    }

    private Iterable<RevCommit> retrieveGitRevCommits( final Repository gitRepository, final String since, final String until )
        throws ChangelogException, IOException, GitAPIException
    {
        Git git = new Git( gitRepository );
        final LogCommand logCommand = git.log();
        if ( since != null )
        {
            final ObjectId sinceObjectId = gitRepository.resolve( since );
            if ( sinceObjectId == null )
            {
                throw new ChangelogException( "The git object reference \"" + since + "\" cannot be resolved" );
            }
            logCommand.not( sinceObjectId );
        }
        if ( until != null )
        {
            final ObjectId untilObjectId = gitRepository.resolve( until );
            if ( untilObjectId == null )
            {
                throw new ChangelogException( "The git object reference \"" + until + "\" cannot be resolved" );
            }
            logCommand.add( untilObjectId );
        }

        return logCommand.call();
    }

    private SortedSet<GitCommit> retrieveGitHubIssueCommits( final Iterable<RevCommit> revCommitIterable )
    {
        final SortedSet<GitCommit> gitHubCommitSet = new TreeSet<>();
        int nbRevCommits = 0;
        for ( RevCommit revCommit : revCommitIterable )
        {
            final String revCommitShortMessage = revCommit.getShortMessage();
            LOGGER.debug( "Commit " + revCommit.getId().getName() + ": " + revCommitShortMessage );

            final Matcher matcher = GITHUB_ISSUE_ID_PATTERN.matcher( revCommitShortMessage );
            final boolean gitHubIdFound = matcher.find();
            if ( gitHubIdFound )
            {
                String gitHubID = matcher.group( 1 );
                gitHubCommitSet.add( new GitCommit( gitHubID, revCommitShortMessage ) );
                LOGGER.debug( "GitHub Issue ID: " + gitHubID );
            }
            nbRevCommits++;
        }

        LOGGER.info( "# Commits retrieved: " + nbRevCommits );
        LOGGER.info( "# Different GitHub Issue IDs found in commits: " + gitHubCommitSet.size() );

        for ( GitCommit gitHubCommit : gitHubCommitSet )
        {
            LOGGER.debug( gitHubCommit.getGitHubIdAsString() + " - " + gitHubCommit.getShortMessage() );
        }
        return gitHubCommitSet;
    }

}
