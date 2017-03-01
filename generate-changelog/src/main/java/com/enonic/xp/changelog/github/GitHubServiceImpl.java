package com.enonic.xp.changelog.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.changelog.ChangelogException;
import com.enonic.xp.changelog.git.GitServiceImpl;
import com.enonic.xp.changelog.git.model.GitCommit;

public class GitHubServiceImpl
    implements GitHubService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GitHubServiceImpl.class );

    @Override
    public List<GHIssue> retrieveGitHubIssues( final String gitDirectoryPath, final Set<GitCommit> issueNumbers )
        throws IOException, ChangelogException
    {
        LOGGER.info( "Retrieving GitHub issues with GitHub issue IDs..." );

        final GitHub gitHub = GitHub.connectAnonymously();
        final GHRepository repo = gitHub.getRepository( GitServiceImpl.findRepoName( gitDirectoryPath ) );
        List<GHIssue> issues = new ArrayList<>( issueNumbers.size() );
        for ( GitCommit commit : issueNumbers )
        {
            GHIssue i = repo.getIssue( commit.getGitHubIdAsInt() );
            issues.add( i );
            LOGGER.debug( i.toString() );
        }

        return issues;
    }

}
