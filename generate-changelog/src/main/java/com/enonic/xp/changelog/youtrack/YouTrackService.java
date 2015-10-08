package com.enonic.xp.changelog.youtrack;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import com.enonic.xp.changelog.git.model.GitCommit;
import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;

public interface YouTrackService
{
    Set<YouTrackIssue> retrieveYouTrackIssues( final Collection<GitCommit> gitCommits, final Predicate<YouTrackIssue> filter )
        throws Exception;
}
