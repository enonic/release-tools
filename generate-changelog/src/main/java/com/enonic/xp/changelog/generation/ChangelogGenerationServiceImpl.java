package com.enonic.xp.changelog.generation;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Predicate;

import org.kohsuke.github.GHIssue;

import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;

public class ChangelogGenerationServiceImpl
    implements ChangelogGenerationService
{
    @Override
    public void generateChangelog( final Collection<GHIssue> gitHubIssueCollection, final String since, final String until,
                                   final Predicate<YouTrackIssue> filter )
        throws IOException
    {
        final ChangelogGenerationJob changelogGenerationJob = new ChangelogGenerationJob( gitHubIssueCollection, since, until, filter );
        changelogGenerationJob.run();
    }
}
