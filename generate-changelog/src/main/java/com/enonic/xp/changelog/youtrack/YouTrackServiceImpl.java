package com.enonic.xp.changelog.youtrack;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;

/**
 * Created by gri on 15/09/15.
 */
public class YouTrackServiceImpl
    implements YouTrackService
{

    @Override
    public Set<YouTrackIssue> retrieveYouTrackIssues( final Collection<String> youTrackICollection, final Predicate<YouTrackIssue> filter )
        throws Exception
    {
        final YouTrackIssuesRetrievalJob youTrackIssuesRetrievalJob = new YouTrackIssuesRetrievalJob( youTrackICollection, filter );
        return youTrackIssuesRetrievalJob.run();
    }
}
