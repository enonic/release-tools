package com.enonic.xp.changelog.github.model;

public class GitHubIssue
    implements Comparable<GitHubIssue>
{
    private int gitHubIssueId;

    private String title;

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

    @Override
    public int compareTo( final GitHubIssue o )
    {

        return this.getTitle().compareTo( o.getTitle() );
    }
}
