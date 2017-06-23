package com.enonic.xp.changelog.zenhub;

import java.util.List;

import com.enonic.xp.changelog.github.model.GitHubIssue;

public class ZenHubServiceImpl implements ZenHubService
{
    String repositoryId;

    @Override
    public void purgeBugsFromEpic( final List<GitHubIssue> bugs, final Integer epicNumber )
    {

    }
}
