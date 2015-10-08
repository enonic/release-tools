package com.enonic.xp.changelog.git;

import java.io.IOException;
import java.util.SortedSet;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.enonic.xp.changelog.ChangelogException;
import com.enonic.xp.changelog.git.model.GitCommit;

public interface GitService
{
    SortedSet<GitCommit> retrieveGitCommits( final String gitDirectoryPath, final String since, final String until )
        throws IOException, GitAPIException, ChangelogException;
}
