package com.enonic.xp.changelog.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
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

    private ArrayList<String> ignoreLabels = new ArrayList<>();

    private List<GitHubIssue> noLabel;

    private HashMap<String, List<GitHubIssue>> issues;

    // TODO: Simplify this service.  Make this class a job that is run, and create a new serviceImpl, that just triggers the job.
    @Override
    public HashMap<String, List<GitHubIssue>> retrieveGitHubIssues( final String gitDirectoryPath, final Set<GitCommit> issueNumbers,
                                                                    final Properties changelogProperties )
        throws IOException, ChangelogException
    {
        LOGGER.info( "Retrieving GitHub issues with GitHub issue IDs..." );

        final GitHub gitHub = GitHub.connect( changelogProperties.getProperty( "user" ), changelogProperties.getProperty( "oAuthToken" ) );
        final GHRepository repo = gitHub.getRepository( GitServiceHelper.findRepoName( gitDirectoryPath ) );

        issues = new HashMap<>( issueNumbers.size() );
        noLabel = new ArrayList<>(  );

        Commit:
        for ( GitCommit commit : issueNumbers )
        {
            GHIssue i = repo.getIssue( commit.getGitHubIdAsInt() );
            verifyAndAddIssue( i );
        }
        for ( GitHubIssue noLabelIssue : noLabel )
        {
            LOGGER.debug( "No label: #" + noLabelIssue.getGitHubIssueId() + " - " + noLabelIssue.getTitle() );
        }
        return issues;
    }

    private void verifyAndAddIssue( final GHIssue i )
        throws IOException
    {
        if (i.isPullRequest()) {
            return;
        }
        // TODO: Somehow, we need to find the parent and include it, if it is a feature.  Either here, or in the commit list.

        LOGGER.debug( i.toString() );
        if (i.getLabels().size() < 1) {
            noLabel.add( new GitHubIssue( i.getNumber(), i.getTitle() ) );
            return;
        }
        for (String ignoreLabel : ignoreLabels)
        {
            for (GHLabel label : i.getLabels()) {
                if (ignoreLabel.equals( label.getName() )) {
                    LOGGER.debug( " -> Ignored because of label: " + label.getName() );
                    return;
                }
            }
        }
        for ( GHLabel label : i.getLabels() )
        {
            List<GitHubIssue> list = issues.get( label.getName() );
            if ( list == null )
            {
                list = new ArrayList<>();
                issues.put( label.getName(), list );
            }
            list.add( new GitHubIssue( i.getNumber(), i.getTitle() ) );
            LOGGER.debug( "  - " + label.getName() + " (" + label.getColor() + ")" );
        }
    }

    @Override
    public void addIgnoreLabel( final String label )
    {
        ignoreLabels.add( label );
    }

}
