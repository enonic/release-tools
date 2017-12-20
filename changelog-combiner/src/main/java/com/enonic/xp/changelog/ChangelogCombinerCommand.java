package com.enonic.xp.changelog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

@Command(name = "changelog-combiner", description = "Combine the changelogs from several GitHub-projects")
public class ChangelogCombinerCommand
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ChangelogCombinerCommand.class );

    @Inject
    public HelpOption helpOption;

    @Option(name = "-o", description = "GitHub organization owner.  Default is 'enonic'")
    public String ownerOrganization = "enonic";

    @Option(name = "-p", description = "Path to folder with changelog files.  Default is ./")
    public String path = "./";

    @Arguments(description = "List of changelog files to be combined.")
    public List<String> changelogFiles;

    public static void main( String... args )
    {
        try
        {
            ChangelogCombinerCommand changelogCombinerCommand = SingleCommand.singleCommand( ChangelogCombinerCommand.class ).parse( args );

            if ( changelogCombinerCommand.helpOption.showHelpIfRequested() )
            {
                return;
            }

            if ((changelogCombinerCommand.changelogFiles == null) || (changelogCombinerCommand.changelogFiles.size() < 1)) {
                LOGGER.debug( "No files to combine!" );
                return;
            }

            changelogCombinerCommand.run();
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
        List<IndividualChangelog> changelogs = new ArrayList<>();
        for (String filename : changelogFiles) {
            changelogs.add( IndividualChangelog.parse(path + filename) );
        }
        LOGGER.debug( "Found changelogs:" );
        for ( IndividualChangelog ic : changelogs ) {
            LOGGER.debug( ic.getProject() );
            for (String section : ic.getEntries().keySet()) {
                LOGGER.debug( "## " + section );
                for (ChangelogEntry ce : ic.getEntries().get( section )) {
                    LOGGER.debug( " - " + ce.getDescription() + " (#" + ce.getIssueNo().toString() + ")." );
                }
            }
        }

    }
}
