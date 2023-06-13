package com.enonic.xp.changelog;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

import com.enonic.xp.changelog.generation.ChangelogGenerationJob;
import com.enonic.xp.changelog.git.GitService;
import com.enonic.xp.changelog.git.GitServiceHelper;
import com.enonic.xp.changelog.git.model.GitCommit;
import com.enonic.xp.changelog.github.GitHubService;
import com.enonic.xp.changelog.github.model.GitHubIssue;


@Command(name = "generate-changelog", description = "Generates the changelog")
public class GenerateChangelogCommand
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GenerateChangelogCommand.class );

    @Inject
    public HelpOption helpOption;

    @Option(name = "-p", description = "Path of the Git repository (default value is the current directory).")
    public String gitDirectoryPath = ".";

    @Option(name = "-s", description = "Since the provided Git reference.")
    public String since;

    @Option(name = "-u", description = "Until the provided Git reference.")
    public String until;

    @Option(name = "--ignore-changelog-check", description = "Ignore the 'Not in Changelog' tag check.")
    public boolean ignoreChangelogCheck;

    @Option( name = "-o", description = "Output Filename")
    public String filename;

    private GitService gitService;

    private GitHubService gitHubService;

    private void init()
        throws IOException
    {
        gitHubService = new GitHubService( GitServiceHelper.findRepoName( gitDirectoryPath ) );

        gitService = new GitService( gitDirectoryPath );
        if ( !ignoreChangelogCheck )  // Double negative logic: Do not add this label to ignorelist, if the ignore check should be ignored! :D
        {
            gitHubService.addIgnoreLabel( "Not in Changelog" );
            gitHubService.addIgnoreLabel( "Won't Fix" );
        }

    }

    public static void main( String... args )
        throws Exception
    {
        GenerateChangelogCommand generateChangelogCommand = SingleCommand.singleCommand( GenerateChangelogCommand.class ).parse( args );

        if ( generateChangelogCommand.helpOption.showHelpIfRequested() )
        {
            return;
        }

        generateChangelogCommand.run();
    }

    private void run()
        throws Exception
    {
        init();

        final Collection<GitCommit> gitCommits = gitService.retrieveGitCommits( since, until );
        final Map<String, List<GitHubIssue>> ghIssues = gitHubService.retrieveGitHubIssues( gitCommits );

        final String projectName = gitHubService.getProjectName();
        new ChangelogGenerationJob( ghIssues, since, until, projectName, filename ).run();
        System.exit( 0 );
    }
}
