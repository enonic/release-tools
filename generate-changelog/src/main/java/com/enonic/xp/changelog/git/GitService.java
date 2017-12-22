package com.enonic.xp.changelog.git;

import java.io.IOException;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.enonic.xp.changelog.ChangelogException;
import com.enonic.xp.changelog.git.model.GitCommit;

public interface GitService
{
    Set<GitCommit> retrieveGitCommits()
        throws IOException, GitAPIException, ChangelogException;
}
