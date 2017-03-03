package com.enonic.xp.changelog.github.model;

public class GitHubIssue
{
    private long gitHubIssueId;

    private String title;

    public GitHubIssue( final long gitHubIssueId, final String title )
    {
        this.gitHubIssueId = gitHubIssueId;
        this.title = title;
    }

    public long getGitHubIssueId()
    {
        return gitHubIssueId;
    }

    public String getTitle()
    {
        return title;
    }
}
