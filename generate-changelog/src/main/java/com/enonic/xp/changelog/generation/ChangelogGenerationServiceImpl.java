package com.enonic.xp.changelog.generation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.enonic.xp.changelog.github.model.GitHubIssue;

public class ChangelogGenerationServiceImpl
    implements ChangelogGenerationService
{
    @Override
    public void generateChangelog( final HashMap<String, List<GitHubIssue>> gitHubIssueCollection, final String since, final String until,
                                   final String projectName )
        throws IOException
    {
        final ChangelogGenerationJob changelogGenerationJob =
            new ChangelogGenerationJob( gitHubIssueCollection, since, until, projectName );
        changelogGenerationJob.run();
    }
}
