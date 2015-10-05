package com.enonic.xp.changelog.git;

import java.io.IOException;
import java.util.SortedSet;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.enonic.xp.changelog.ChangelogException;

public interface GitService
{
    SortedSet<String> retrieveYouTrackIds( final String gitDirectoryPath, final String since, final String until )
        throws IOException, GitAPIException, ChangelogException;
}
