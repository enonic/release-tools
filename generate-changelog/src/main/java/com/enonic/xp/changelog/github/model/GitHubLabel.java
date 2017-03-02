package com.enonic.xp.changelog.github.model;

public class GitHubLabel
{
    final private String title;

    final private String color;

    public GitHubLabel( final String title, final String color )
    {
        this.title = title;
        this.color = color;
    }

    public String getTitle()
    {
        return title;
    }

    public String getColor()
    {
        return color;
    }
}
