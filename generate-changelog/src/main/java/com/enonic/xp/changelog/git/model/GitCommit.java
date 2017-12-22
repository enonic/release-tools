package com.enonic.xp.changelog.git.model;

public class GitCommit
    implements Comparable<GitCommit>
{
    private Integer gitHubId;

    private String shortMessage;

    public GitCommit( final Integer gitHubId, final String shortMessage )
    {
        this.gitHubId = gitHubId;
        this.shortMessage = shortMessage;
    }

    public GitCommit( final String gitHubId, final String shortMessage )
    {
        this.gitHubId = parseGitHubId( gitHubId );
        this.shortMessage = shortMessage;
    }

//    public GitCommit( final Integer gitHubId, final String shortMessage )
//    {
//        this.gitHubId = gitHubId;
//        this.shortMessage = shortMessage;
//    }

    public String getGitHubIdAsString()
    {
        return "#" + gitHubId.toString();
    }

    public Integer getGitHubIdAsInt()
    {
        return gitHubId;
    }

    private Integer parseGitHubId( final String gitHubId )
    {
        if ( gitHubId.charAt( 0 ) == '#' )
        {
            return Integer.parseInt( gitHubId.substring( 1 ) );
        }
        else
        {
            throw new NumberFormatException( "Not a valid GitHubId: " + gitHubId );
        }
    }

    public String getShortMessage()
    {
        return shortMessage;
    }

    @Override
    public int compareTo( final GitCommit gitCommit )
    {
        return gitHubId.compareTo( gitCommit.getGitHubIdAsInt() );
    }
}
