package com.enonic.xp.changelog;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

import com.enonic.xp.changelog.generation.ChangelogGenerationService;
import com.enonic.xp.changelog.generation.ChangelogGenerationServiceImpl;
import com.enonic.xp.changelog.git.GitService;
import com.enonic.xp.changelog.git.GitServiceImpl;
import com.enonic.xp.changelog.git.model.GitCommit;
import com.enonic.xp.changelog.github.GitHubService;
import com.enonic.xp.changelog.github.GitHubServiceImpl;
import com.enonic.xp.changelog.github.model.GitHubIssue;


@Command(name = "generate-changelog", description = "Generates the changelog")
public class GenerateChangelogCommand
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GenerateChangelogCommand.class );

    @Inject
    private HelpOption helpOption;

    @Option(name = "-f", description = "Full path and file name of properties file.  Default is './changelog.properties'")
    private String propertiesFile = "./changelog.properties";

    @Option(name = "-p", description = "Path of the Git repository (default value is the current directory).")
    private String gitDirectoryPath = ".";

    @Option(name = "-s", description = "Since the provided Git reference.")
    private String since;

    @Option(name = "-u", description = "Until the provided Git reference.")
    private String until;

    @Option(name = "--ignore-changelog-check", description = "Ignore the ZenHub 'Not in Changelog' tag check.")
    private boolean ignoreChangelogCheck;

    private GitService gitService;

    private GitHubService gitHubService;

    private ChangelogGenerationService changelogGenerationService;

    private void init()
        throws IOException, ChangelogException
    {
        gitService = new GitServiceImpl( gitDirectoryPath, since, until );
        gitHubService = new GitHubServiceImpl( gitDirectoryPath, getPropertiesFromFile() );
        if ( !ignoreChangelogCheck )  // Double negative logic: Do not add this label to ignorelist, if the ignore check should be ignored! :D
        {
            gitHubService.addIgnoreLabel( "Not in Changelog" );
            gitHubService.addIgnoreLabel( "Won't Fix" );
        }

        changelogGenerationService = new ChangelogGenerationServiceImpl();
    }

    public static void main( String... args )
    {
        try
        {
            GenerateChangelogCommand generateChangelogCommand = SingleCommand.singleCommand( GenerateChangelogCommand.class ).parse( args );

            if ( generateChangelogCommand.helpOption.showHelpIfRequested() )
            {
                return;
            }

            generateChangelogCommand.run();
        }
        catch ( Exception e )
        {
            LOGGER.error( "Error while generating the change log: " + e.getMessage() );
            LOGGER.debug( "Error details: ", e );
        }
    }

    private void run()
        throws Exception
    {
        init();

        final Set<GitCommit> gitCommits = gitService.retrieveGitCommits();
        final HashMap<String, List<GitHubIssue>> ghIssues = gitHubService.retrieveGitHubIssues( gitCommits );

        changelogGenerationService.generateChangelog( ghIssues, since, until, gitHubService.getProjectName() );
        System.exit( 1 );
    }

    private Properties getPropertiesFromFile()
        throws IOException
    {
        FileReader changelogFileReader = new FileReader( propertiesFile );
        final Properties props = new Properties();
        props.load( changelogFileReader );
        return props;
    }
}