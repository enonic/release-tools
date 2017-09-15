package com.enonic.xp.changelog.generation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.enonic.xp.changelog.github.model.GitHubIssue;

public interface ChangelogGenerationService
{
    void generateChangelog( HashMap<String, List<GitHubIssue>> gitHubIssueCollection, String since, String until, final String projectName )
        throws IOException;
}
