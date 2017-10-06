package com.enonic.xp.changelog.git;

import java.io.IOException;
import java.util.Set;

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

    private final String gitDirectoryPath;

    private final String since;

    private final String until;

    public GitServiceImpl( final String gitDirectoryPath, final String since, final String until )
    {
        this.gitDirectoryPath = gitDirectoryPath;
        this.since = since;
        this.until = until;
    }

    @Override
    public Set<GitCommit> retrieveGitCommits()
        throws IOException, GitAPIException, ChangelogException
    {
        LOGGER.info( "Retrieving Git commits with GitHub issue IDs..." );

        //Retrieves the Git repository
        final Repository gitRepository = GitServiceHelper.retrieveGitRepository( gitDirectoryPath );

        //Retrieves the Git commits
        final Iterable<RevCommit> revCommitIterable = retrieveGitRevCommits( gitRepository, since, until );

        //Parses the Git commits
        final Set<GitCommit> gitHubIssueCommits = GitServiceHelper.retrieveGitHubIssueCommits( revCommitIterable );

        LOGGER.info( gitHubIssueCommits.size() + " Git commits with GitHub Issue IDs retrieved." );

        Set<GitCommit> gitCommitsNoPRs = GitServiceHelper.filterPullRequests( gitHubIssueCommits );

        LOGGER.info(gitCommitsNoPRs.size() + " commits left after filtering out Pull Requests." );

        return gitCommitsNoPRs;
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
}
