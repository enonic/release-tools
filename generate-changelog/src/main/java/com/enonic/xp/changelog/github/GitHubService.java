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
    HashMap<String, List<GitHubIssue>> retrieveGitHubIssues( final Set<GitCommit> issueNumbers )
        throws IOException, ChangelogException;

    /**
     * Labels added with this ignore method, will cause all issues where this label is one of the labels, to be ignored completely,
     * no matter what other labels the issue have.
     *
     * @param label
     */
    void addIgnoreLabel( final String label );

    /**
     * This is the GitHub-internal numeric ID.
     *
     * @return The GitHub repository ID.
     */
    Integer getRepoId ();
}
