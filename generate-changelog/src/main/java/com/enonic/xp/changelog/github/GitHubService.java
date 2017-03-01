package com.enonic.xp.changelog.github;

import java.util.SortedSet;

import com.enonic.xp.changelog.git.model.GitCommit;

public interface GitHubService
{
    SortedSet<GitCommit> retrieveGitHubIssues( final String gitDirectoryPath, final String since, final String until );
}
