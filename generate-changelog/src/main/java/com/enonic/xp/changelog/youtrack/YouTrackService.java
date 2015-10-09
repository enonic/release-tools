package com.enonic.xp.changelog.youtrack;

import java.util.Collection;
import java.util.Set;

import com.enonic.xp.changelog.git.model.GitCommit;
import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;

public interface YouTrackService
{
    Set<YouTrackIssue> retrieveYouTrackIssues( final Collection<GitCommit> gitCommits )
        throws Exception;
}
