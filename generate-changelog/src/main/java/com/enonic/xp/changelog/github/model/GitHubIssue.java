package com.enonic.xp.changelog.github.model;

import java.util.Set;

public class GitHubIssue
{
    private long gitHubIssueId;

    private String title;

    private Set<GitHubLabel> labels;

    public GitHubIssue( final long gitHubIssueId, final String title )
    {
        this.gitHubIssueId = gitHubIssueId;
        this.title = title;
    }

    public void addLabel (final String title, final String color) {
        labels.add( new GitHubLabel( title, color ) );
    }

    public long getGitHubIssueId()
    {
        return gitHubIssueId;
    }

    public String getTitle()
    {
        return title;
    }

    public Set<GitHubLabel> getLabels()
    {
        return labels;
    }
}
