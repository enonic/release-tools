package com.enonic.xp.changelog;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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

    private static final String systemNewLineChar = System.getProperty( "line.separator" );

    @Inject
    public HelpOption helpOption;

    @Option(name = "-w", description = "GitHub organization owner.  Default is 'enonic'")
    public String ownerOrganization = "enonic";

    @Option(name = "-p", description = "Path to folder with changelog files.  Default is current directory")
    public String path = ".";

    @Option(name = "-o", description = "Output filename. Default is composing a name from all input files.")
    public String filename = null;

    @Arguments(description = "List of changelog files to be combined.  Wildcards may be used.")
    public List<String> changelogFiles;

    public static void main( String... args )
        throws Exception
    {
        ChangelogCombinerCommand changelogCombinerCommand = SingleCommand.singleCommand( ChangelogCombinerCommand.class ).parse( args );

        if ( changelogCombinerCommand.helpOption.showHelpIfRequested() )
        {
            return;
        }

        if ( ( changelogCombinerCommand.changelogFiles == null ) || ( changelogCombinerCommand.changelogFiles.size() < 1 ) )
        {
            LOGGER.warn( "No files to combine!" );
            return;
        }

        changelogCombinerCommand.run();
    }

    private void run()
        throws Exception
    {
        List<IndividualChangelog> changelogs = new ArrayList<>();
        for ( String filename : changelogFiles )
        {
            if ( filename.contains( "*" ) )
            {
                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream( Path.of( path ), filename ))
                {
                    for ( Path file : dirStream )
                    {
                        changelogs.add( IndividualChangelog.parse( file ) );
                        System.out.println( "Including " + file + " in changelog!" );
                    }
                }
            }
            else
            {
                changelogs.add( IndividualChangelog.parse( Path.of( path, filename )  ) );
                System.out.println( "Including " + filename );
            }
        }

        IndividualChangelog completeChangelog = combineChangelogs( changelogs );
        String completeChangelogFileName = composeFileName( changelogs );
        LOGGER.debug( "Changelog file: " + completeChangelogFileName );
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
        return mergeDuplicates( combinedEntries );
    }

    private ArrayList<ChangelogEntry> mergeDuplicates( final ArrayList<ChangelogEntry> combinedEntries )
    {
        if ( combinedEntries.size() < 1 )
        {
            return combinedEntries;
        }
        ChangelogEntry entry = combinedEntries.get( 0 );
        for ( int i = 1; i < combinedEntries.size(); i++ )
        {
            ChangelogEntry nextEntry = combinedEntries.get( i );
            if ( entry.getDescription().equals( nextEntry.getDescription() ) )
            {
                String mergedIssueNo = entry.getIssueNo() + ", " + nextEntry.getIssueNo();
                ChangelogEntry mergedEntry = new ChangelogEntry( entry.getDescription(), mergedIssueNo );
                combinedEntries.remove( entry );
                combinedEntries.remove( nextEntry );
                combinedEntries.add( mergedEntry );
                Collections.sort( combinedEntries );
                mergeDuplicates( combinedEntries );
                return combinedEntries;
            }
            entry = nextEntry;
        }
        return combinedEntries;
    }

    private String composeFileName( final List<IndividualChangelog> changelogs )
    {
        StringBuilder outputFilename = new StringBuilder();
        if ( this.filename != null )
        {
            outputFilename.append( this.filename );
            if ( !this.filename.endsWith( ".md" ) )
            {
                outputFilename.append( ".md" );
            }
        }
        else
        {
            outputFilename.append( "ccl_" );
            for ( IndividualChangelog ic : changelogs )
            {
                outputFilename.append( ic.getProject() ).append( '_' );
            }
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd-HHmmss" );
            outputFilename.append( sdf.format( today ) );
            outputFilename.append( ".md" );
        }
        return outputFilename.toString();
    }

    private void writeCompleteChangelogToFile( final IndividualChangelog completeChangelog, final String completeChangelogFileName )
        throws IOException
    {
        final Path file = Path.of( completeChangelogFileName );

        Files.writeString( file, "# Changelog" + systemNewLineChar + createSection( "Features", completeChangelog ) +
            createSection( "Improvements", completeChangelog ) + createSection( "Bugs", completeChangelog ) +
            createSection( "Refactorings", completeChangelog ) );
    }

    private String createSection( final String section, final IndividualChangelog completeChangelog )
    {
        final ArrayList<ChangelogEntry> changelogEntries = completeChangelog.getEntries().get( section );
        if ( changelogEntries.size() < 1 )
        {
            return "";
        }
        StringBuilder sb = new StringBuilder( systemNewLineChar );
        sb.append( "## " ).append( section ).append( systemNewLineChar );
        for ( ChangelogEntry ce : changelogEntries )
        {
            sb.append( " - " ).append( ce.getDescription() ).append( " (" ).append( ce.getIssueNo() ).append( ")" ).append(
                systemNewLineChar );
        }
        return sb.toString();
    }
}
