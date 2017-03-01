package com.enonic.xp.changelog.generation;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Predicate;

import org.kohsuke.github.GHIssue;

import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;

public interface ChangelogGenerationService
{
    void generateChangelog( Collection<GHIssue> gitHubIssueCollection, String since, String until,
                            final Predicate<YouTrackIssue> filter )
        throws IOException;
}
