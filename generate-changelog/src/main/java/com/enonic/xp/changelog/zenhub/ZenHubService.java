package com.enonic.xp.changelog.zenhub;

import java.util.List;

import com.enonic.xp.changelog.github.model.GitHubIssue;

public interface ZenHubService
{
    void purgeBugsFromEpic ( final List<GitHubIssue> bugs, Integer epicNumber );
}
