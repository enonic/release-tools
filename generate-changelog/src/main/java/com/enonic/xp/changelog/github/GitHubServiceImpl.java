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

public class GitHubServiceImpl
    implements GitHubService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GitHubServiceImpl.class );

    @Override
    public HashMap<String, List<GitHubIssue>> retrieveGitHubIssues( final String gitDirectoryPath, final Set<GitCommit> issueNumbers )
        throws IOException, ChangelogException
    {
        LOGGER.info( "Retrieving GitHub issues with GitHub issue IDs..." );

        final GitHub gitHub = GitHub.connect( "jsi@enonic.com", "1c6b6e795419400c39b1dde01778a25543c50e5a" );
        final GHRepository repo = gitHub.getRepository( GitServiceHelper.findRepoName( gitDirectoryPath ) );

        HashMap<String, List<GitHubIssue>> issues = new HashMap<>( issueNumbers.size() );
        for ( GitCommit commit : issueNumbers )
        {
            GHIssue i = repo.getIssue( commit.getGitHubIdAsInt() );
            LOGGER.debug( i.toString() );
            for ( GHLabel label : i.getLabels() )
            {
                List<GitHubIssue> list = issues.get( label.getName() );
                if (list == null) {
                    list = new ArrayList<GitHubIssue>(  );
                    issues.put( label.getName(), list );
                }
                list.add( new GitHubIssue( i.getNumber(), i.getTitle() ) );
                LOGGER.debug( "  - " + label.getName() + " (" + label.getColor() + ")");
            }
        }
        return issues;
    }

}
