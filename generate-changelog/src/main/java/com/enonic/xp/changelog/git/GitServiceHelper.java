package com.enonic.xp.changelog.git;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.changelog.ChangelogException;
import com.enonic.xp.changelog.git.model.GitCommit;

public class GitServiceHelper
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GitServiceHelper.class );

    private static final Pattern GITHUB_ISSUE_ID_PATTERN = Pattern.compile( "(#[0-9]+)" );

    private static String PR_START_STRING = "Merge pull request #";

    static Repository retrieveGitRepository( final String gitDirectoryPath )
        throws ChangelogException, IOException
    {
        final File gitDirectory = new File( gitDirectoryPath, ".git" );
        if ( !gitDirectory.isDirectory() )
        {
            throw new ChangelogException( "\"" + gitDirectory.getAbsolutePath() + "\" is not a directory" );
        }
        final FileRepositoryBuilder fileRepositoryBuilder = new FileRepositoryBuilder().setMustExist( true ).setGitDir( gitDirectory );
        return fileRepositoryBuilder.build();
    }

    public static String findRepoName( final String gitDirectoryPath )
        throws ChangelogException, IOException
    {
        //Retrieves the Git repository
        final Repository gitRepository = retrieveGitRepository( gitDirectoryPath );

        final StoredConfig config = gitRepository.getConfig();
        final String remoteURL = config.getString( "remote", "origin", "url" );
        String[] tokens = remoteURL.split( ":" );
        String repoName;
        if ( tokens[1].endsWith( ".git" ) )
        {
            repoName = tokens[1].substring( 0, tokens[1].length() - 4 );
        }
        else
        {
            throw new ChangelogException( "Can't extract repoName from remoteURL: " + remoteURL );
        }
        return repoName;
    }

    static Set<GitCommit> filterPullRequests( Set<GitCommit> gitCommitIssues )
    {
        return gitCommitIssues.stream().filter( gitCommit -> !gitCommit.getShortMessage().startsWith( PR_START_STRING ) ).collect(
            Collectors.toSet() );
    }

    static Set<GitCommit> retrieveGitHubIssueCommits( final Iterable<RevCommit> revCommitIterable )
    {
        final Set<GitCommit> gitHubCommitSet = new TreeSet<>();
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
