package com.enonic.xp.changelog.github.model;

public class GitHubIssue
{
    private final int gitHubIssueId;

    private final String title;

    public GitHubIssue( final int gitHubIssueId, final String title )
    {
        this.gitHubIssueId = gitHubIssueId;
        this.title = title;
    }

    public int getGitHubIssueId()
    {
        return gitHubIssueId;
    }

    public String getTitle()
    {
        return title;
    }
}
