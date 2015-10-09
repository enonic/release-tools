package com.enonic.xp.changelog.generation;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Predicate;

import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;

public class ChangelogGenerationServiceImpl
    implements ChangelogGenerationService
{
    @Override
    public void generateChangelog( final Collection<YouTrackIssue> youTrackIssueCollection, final String since, final String until,
                                   final Predicate<YouTrackIssue> filter )
        throws IOException
    {
        final ChangelogGenerationJob changelogGenerationJob = new ChangelogGenerationJob( youTrackIssueCollection, since, until, filter );
        changelogGenerationJob.run();
    }
}
