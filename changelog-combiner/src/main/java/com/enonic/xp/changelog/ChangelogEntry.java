package com.enonic.xp.changelog;

public class ChangelogEntry implements Comparable<ChangelogEntry>
{
    private String description;

    private String issueNo;

    ChangelogEntry( final String description, final String issueNo )
    {
        this.description = description;
        this.issueNo = issueNo;
    }

    String getDescription()
    {
        return description;
    }

    String getIssueNo()
    {
        return issueNo;
    }

    @Override
    public int compareTo( final ChangelogEntry o )
    {
        return this.description.compareTo( o.getDescription() );
    }
}
