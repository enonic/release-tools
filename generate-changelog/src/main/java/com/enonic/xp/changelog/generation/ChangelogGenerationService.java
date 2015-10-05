package com.enonic.xp.changelog.generation;

import java.io.IOException;
import java.util.Collection;

import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;

public interface ChangelogGenerationService
{
    void generateChangelog( Collection<YouTrackIssue> youTrackIssueCollection, String since, String until )
        throws IOException;
}
