package com.enonic.xp.changelog.git.model;

public class GitCommit
    implements Comparable<GitCommit>
{
    private String youTrackId;

    private String shortMessage;

    public GitCommit( final String youTrackId, final String shortMessage )
    {
        this.youTrackId = youTrackId;
        this.shortMessage = shortMessage;
    }

    public String getYouTrackId()
    {
        return youTrackId;
    }

    public String getShortMessage()
    {
        return shortMessage;
    }

    @Override
    public int compareTo( final GitCommit gitCommit )
    {
        return youTrackId.compareTo( gitCommit.getYouTrackId() );
    }
}
