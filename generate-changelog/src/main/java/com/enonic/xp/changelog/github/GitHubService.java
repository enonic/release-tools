package com.enonic.xp.changelog.github;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.kohsuke.github.GHIssue;

import com.enonic.xp.changelog.ChangelogException;
import com.enonic.xp.changelog.git.model.GitCommit;

public interface GitHubService
{
    List<GHIssue> retrieveGitHubIssues( final String gitDirectoryPath, final Set<GitCommit> issueNumbers )
        throws IOException, ChangelogException;
}
