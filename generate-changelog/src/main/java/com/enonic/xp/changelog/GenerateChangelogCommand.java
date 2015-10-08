package com.enonic.xp.changelog;


import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.jgit.api.errors.GitAPIException;
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
import com.enonic.xp.changelog.youtrack.YouTrackService;
import com.enonic.xp.changelog.youtrack.YouTrackServiceImpl;
import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;


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

    @Option(name = "--ignore-field-check", description = "Ignore the YouTrack Changelog field check.")
    public boolean ignoreFieldCheck;

    private YouTrackService youTrackService;

    private GitService gitService;

    private ChangelogGenerationService changelogGenerationService;

    public GenerateChangelogCommand()
    {
        youTrackService = new YouTrackServiceImpl();
        gitService = new GitServiceImpl();
        changelogGenerationService = new ChangelogGenerationServiceImpl();
    }

    public static void main( String... args )
        throws IOException, GitAPIException
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

    public void run()
        throws Exception
    {
        final Set<GitCommit> gitCommits = gitService.retrieveGitCommits( gitDirectoryPath, since, until );
        final Set<YouTrackIssue> youTrackIssueSet =
            youTrackService.retrieveYouTrackIssues( gitCommits, youTrackIssue -> ignoreFieldCheck || youTrackIssue.mustBeLogged() );
        changelogGenerationService.generateChangelog( youTrackIssueSet, since, until );
    }
}