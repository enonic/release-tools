package com.enonic.xp.changelog.github;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.enonic.xp.changelog.ChangelogException;
import com.enonic.xp.changelog.git.model.GitCommit;
import com.enonic.xp.changelog.github.model.GitHubIssue;

public interface GitHubService
{
    HashMap<String, List<GitHubIssue>> retrieveGitHubIssues( final String gitDirectoryPath, final Set<GitCommit> issueNumbers )
        throws IOException, ChangelogException;
}
