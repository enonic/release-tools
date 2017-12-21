package com.enonic.xp.changelog;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharSink;
import com.google.common.io.Files;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

@Command(name = "changelog-combiner", description = "Combine the changelogs from several GitHub-projects")
public class ChangelogCombinerCommand
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ChangelogCombinerCommand.class );

    private static final String systemNewLineChar = System.getProperty( "line.separator" );

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

            if ( ( changelogCombinerCommand.changelogFiles == null ) || ( changelogCombinerCommand.changelogFiles.size() < 1 ) )
            {
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
        for ( String filename : changelogFiles )
        {
            changelogs.add( IndividualChangelog.parse( path + filename ) );
        }

        IndividualChangelog completeChangelog = combineChangelogs( changelogs );
        String completeChangelogFileName = composeFileName( changelogs );
        writeCompleteChangelogToFile( completeChangelog, completeChangelogFileName );
    }

    private IndividualChangelog combineChangelogs( final List<IndividualChangelog> changelogs )
    {
        HashMap<String, ArrayList<ChangelogEntry>> completeChangelog = new HashMap<>();
        completeChangelog.put( "Features", createSection( "Features", changelogs ) );
        completeChangelog.put( "Improvements", createSection( "Improvements", changelogs ) );
        completeChangelog.put( "Bugs", createSection( "Bugs", changelogs ) );
        completeChangelog.put( "Refactorings", createSection( "Refactorings", changelogs ) );
        return new IndividualChangelog( "Complete", completeChangelog );
    }

    private ArrayList<ChangelogEntry> createSection( final String section, final List<IndividualChangelog> changelogs )
    {
        ArrayList<ChangelogEntry> combinedEntries = new ArrayList<>();
        for ( IndividualChangelog ic : changelogs )
        {
            ArrayList<ChangelogEntry> sectionEntries = ic.getEntries().get( section );
            if ( sectionEntries != null )
            {
                for ( ChangelogEntry entry : sectionEntries )
                {
                    combinedEntries.add(
                        new ChangelogEntry( entry.getDescription(), ownerOrganization + "/" + ic.getProject() + entry.getIssueNo() ) );
                }
            }
        }
        Collections.sort( combinedEntries );
        return combinedEntries;
    }

    private static String composeFileName( final List<IndividualChangelog> changelogs )
    {
        StringBuilder filename = new StringBuilder( "changelog_" );
        for ( IndividualChangelog ic : changelogs )
        {
            filename.append( ic.getProject() ).append( '_' );
        }
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd-hhmmss" );
        filename.append( sdf.format( today ) );
        return filename.toString();
    }

    private void writeCompleteChangelogToFile( final IndividualChangelog completeChangelog, final String completeChangelogFileName )
        throws IOException
    {
        final File file = new File( completeChangelogFileName );

        //Writes the output file content
        final CharSink charSink = Files.asCharSink( file, Charset.forName( "UTF-8" ) );

        charSink.write( "# Changelog" + systemNewLineChar +
                            createSection( "Features", completeChangelog ) +
                            createSection( "Improvements", completeChangelog ) +
                            createSection( "Bugs", completeChangelog ) +
                            createSection( "Refactorings", completeChangelog ) );
    }

    private String createSection( final String section, final IndividualChangelog completeChangelog )
    {
        StringBuilder sb = new StringBuilder( systemNewLineChar );
        sb.append( "## " ).append( section ).append( systemNewLineChar );
        for ( ChangelogEntry ce : completeChangelog.getEntries().get( section ) )
        {
            sb.append( " - " ).append( ce.getDescription() ).append( " (" ).append( ce.getIssueNo() ).append( ")" ).append(
                systemNewLineChar );
        }
        return sb.toString();
    }
}