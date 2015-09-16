package com.enonic.xp.changelog.git;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.enonic.xp.changelog.ChangelogException;

/**
 * Created by gri on 15/09/15.
 */
public class GitServiceImpl
    implements GitService
{
    private static final Logger LOGGER = LogManager.getLogger( GitServiceImpl.class );

    private static final Pattern YOUTRACK_ID_PATTERN = Pattern.compile( "^(XP-[0-9]+) ", Pattern.CASE_INSENSITIVE );

    @Override
    public SortedSet<String> retrieveYouTrackIds( final String gitDirectoryPath, final String since, final String until )
        throws IOException, GitAPIException, ChangelogException
    {
        LOGGER.info( "Retrieving YouTrack IDs..." );

        //Retrieves the Git repository
        final Repository gitRepository = retrieveGitRepository( gitDirectoryPath );

        //Retrieves the Git Rev commits
        final Iterable<RevCommit> revCommitIterable = retrieveGitRevCommits( gitRepository, since, until );

        //Parses the Git commits
        final SortedSet<String> youTrackIdSet = retrieveYouTrackIds( revCommitIterable );

        LOGGER.info( youTrackIdSet.size() + " YouTrack IDs retrieved." );
        return youTrackIdSet;
    }

    private Repository retrieveGitRepository( final String gitDirectoryPath )
        throws ChangelogException, IOException
    {
        final File gitDirectory = new File( gitDirectoryPath );
        if ( !gitDirectory.isDirectory() )
        {
            throw new ChangelogException( "\"" + gitDirectoryPath + "\" is not a directory" );
        }
        final FileRepositoryBuilder fileRepositoryBuilder = new FileRepositoryBuilder().setMustExist( true ).setGitDir( gitDirectory );
        return fileRepositoryBuilder.build();
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

    private SortedSet<String> retrieveYouTrackIds( final Iterable<RevCommit> revCommitIterable )
    {
        final SortedSet<String> youTrackIdSet = new TreeSet<>();
        int nbRevCommits = 0;
        for ( RevCommit revCommit : revCommitIterable )
        {
            final String revCommitShortMessage = revCommit.getShortMessage();
            LOGGER.debug( "Commit " + revCommit.getId().getName() + ": " + revCommitShortMessage );

            final Matcher matcher = YOUTRACK_ID_PATTERN.matcher( revCommitShortMessage );
            final boolean youTrackIdFound = matcher.find();
            if ( youTrackIdFound )
            {
                String youTrackID = matcher.group( 1 );
                youTrackIdSet.add( youTrackID );
                LOGGER.debug( "YouTrack ID: " + youTrackID );
            }

            nbRevCommits++;
        }
        LOGGER.debug( "# Commits retrieved: " + nbRevCommits );
        LOGGER.debug( "# YouTrack IDs retrieved: " + youTrackIdSet.size() );

        return youTrackIdSet;
    }
}
