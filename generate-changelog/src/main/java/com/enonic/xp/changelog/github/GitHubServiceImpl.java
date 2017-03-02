package com.enonic.xp.changelog.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.changelog.ChangelogException;
import com.enonic.xp.changelog.git.GitServiceHelper;
import com.enonic.xp.changelog.git.model.GitCommit;
import com.enonic.xp.changelog.github.model.GitHubIssue;
import com.enonic.xp.changelog.github.model.GitHubLabel;

public class GitHubServiceImpl
    implements GitHubService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GitHubServiceImpl.class );

    @Override
    public HashMap<GitHubLabel, List<GitHubIssue>> retrieveGitHubIssues( final String gitDirectoryPath, final Set<GitCommit> issueNumbers )
        throws IOException, ChangelogException
    {
        LOGGER.info( "Retrieving GitHub issues with GitHub issue IDs..." );

        final GitHub gitHub = GitHub.connectAnonymously();
        final GHRepository repo = gitHub.getRepository( GitServiceHelper.findRepoName( gitDirectoryPath ) );

        HashMap<GitHubLabel, List<GitHubIssue>> issues = new HashMap<>( issueNumbers.size() );
        for ( GitCommit commit : issueNumbers )
        {
            GHIssue i = repo.getIssue( commit.getGitHubIdAsInt() );
            LOGGER.debug( i.toString() );
            for ( GHLabel label : i.getLabels() )
            {
                List<GitHubIssue> list = issues.get( label.getName() );
                if (list == null) {
                    list = new ArrayList<GitHubIssue>(  );
                    issues.put( new GitHubLabel( label.getName(), label.getColor() ), list );
                }
                list.add( new GitHubIssue( i.getId(), i.getTitle() ) );
                LOGGER.debug( "  - " + label.getName() + " (" + label.getColor() + ")");
            }
        }
        return issues;
    }

}
