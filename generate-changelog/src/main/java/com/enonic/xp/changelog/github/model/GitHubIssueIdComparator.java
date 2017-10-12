package com.enonic.xp.changelog.github.model;

import java.util.Comparator;

public class GitHubIssueIdComparator<T>
    implements Comparator<GitHubIssue>
{

    @Override
    public int compare( final GitHubIssue o1, final GitHubIssue o2 )
    {
        return o1.getGitHubIssueId() - o2.getGitHubIssueId();
    }
}
