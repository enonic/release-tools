package com.enonic.xp.changelog;

public class ChangelogEntry
{
    private String description;

    private Integer issueNo;

    public ChangelogEntry( final String description, final Integer issueNo )
    {
        this.description = description;
        this.issueNo = issueNo;
    }

    public String getDescription()
    {
        return description;
    }

    public Integer getIssueNo()
    {
        return issueNo;
    }
}
