package com.enonic.xp.changelog.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.enonic.xp.changelog.ChangelogException;

public class GitServiceHelper
{
    public static Repository retrieveGitRepository( final String gitDirectoryPath )
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
}
