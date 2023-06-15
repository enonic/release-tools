import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangelogCombinerCommand
{
    private static final String systemNewLineChar = System.getProperty( "line.separator" );

    public String ownerOrganization = "enonic";

    public String path = ".";

    public String filename = null;

    public List<String> changelogFiles = new ArrayList<>();

    public static void main( String... args )
        throws Exception
    {
        ChangelogCombinerCommand changelogCombinerCommand = ChangelogCombinerCommand.newInstance( args );

        if ( ( changelogCombinerCommand.changelogFiles == null ) || ( changelogCombinerCommand.changelogFiles.isEmpty() ) )
        {
            throw new IllegalArgumentException( "No files to combine!" );
        }

        changelogCombinerCommand.run();
    }

    private static ChangelogCombinerCommand newInstance( String[] args )
    {
        final ChangelogCombinerCommand command = new ChangelogCombinerCommand();
        for ( int i = 0; i < args.length; i++ )
        {
            switch ( args[i] )
            {
                case "-w":
                    command.ownerOrganization = args[++i];
                    break;
                case "-p":
                    command.path = args[++i];
                    break;
                case "-o":
                    command.filename = args[++i];
                    break;
                default:
                    command.changelogFiles.add( args[i] );
                    break;
            }
        }
        return command;
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
                        changelogs.add( parse( file ) );
                        System.out.println( "Including " + file + " in changelog!" );
                    }
                }
            }
            else
            {
                changelogs.add( parse( Path.of( path, filename ) ) );
                System.out.println( "Including " + filename );
            }
        }

        var completeChangelog = combineChangelogs( changelogs );
        String completeChangelogFileName = composeFileName( changelogs );
        System.out.println( "Changelog file: " + completeChangelogFileName );

        Files.writeString( Path.of( completeChangelogFileName ),
                           "# Changelog" + systemNewLineChar + createSection( "Features", completeChangelog ) +
                               createSection( "Improvements", completeChangelog ) + createSection( "Bugs", completeChangelog ) +
                               createSection( "Refactorings", completeChangelog ) );
    }

    private Map<String, List<ChangelogEntry>> combineChangelogs( final List<IndividualChangelog> changelogs )
    {
        final Map<String, List<ChangelogEntry>> completeChangelog = new HashMap<>();
        completeChangelog.put( "Features", createSection( "Features", changelogs ) );
        completeChangelog.put( "Improvements", createSection( "Improvements", changelogs ) );
        completeChangelog.put( "Bugs", createSection( "Bugs", changelogs ) );
        completeChangelog.put( "Refactorings", createSection( "Refactorings", changelogs ) );
        return completeChangelog;
    }

    private List<ChangelogEntry> createSection( final String section, final List<IndividualChangelog> changelogs )
    {
        ArrayList<ChangelogEntry> combinedEntries = new ArrayList<>();
        for ( IndividualChangelog ic : changelogs )
        {
            List<ChangelogEntry> sectionEntries = ic.getEntries().get( section );
            if ( sectionEntries != null )
            {
                for ( ChangelogEntry entry : sectionEntries )
                {
                    combinedEntries.add(
                        new ChangelogEntry( entry.description, ownerOrganization + "/" + ic.getProject() + entry.issueNo ) );
                }
            }
        }
        combinedEntries.sort( Comparator.comparing( c -> c.description ) );
        return mergeDuplicates( combinedEntries );
    }

    private List<ChangelogEntry> mergeDuplicates( final List<ChangelogEntry> combinedEntries )
    {
        if ( combinedEntries.isEmpty() )
        {
            return combinedEntries;
        }
        ChangelogEntry entry = combinedEntries.get( 0 );
        for ( int i = 1; i < combinedEntries.size(); i++ )
        {
            ChangelogEntry nextEntry = combinedEntries.get( i );
            if ( entry.description.equals( nextEntry.description ) )
            {
                String mergedIssueNo = entry.issueNo + ", " + nextEntry.issueNo;
                ChangelogEntry mergedEntry = new ChangelogEntry( entry.description, mergedIssueNo );
                combinedEntries.remove( entry );
                combinedEntries.remove( nextEntry );
                combinedEntries.add( mergedEntry );
                combinedEntries.sort( Comparator.comparing( c -> c.description ) );
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

    private String createSection( final String section, final Map<String, List<ChangelogEntry>> completeChangelog )
    {
        final List<ChangelogEntry> changelogEntries = completeChangelog.get( section );
        if ( changelogEntries.isEmpty() )
        {
            return "";
        }
        StringBuilder sb = new StringBuilder( systemNewLineChar );
        sb.append( "## " ).append( section ).append( systemNewLineChar );
        for ( ChangelogEntry ce : changelogEntries )
        {
            sb.append( " - " ).append( ce.description ).append( " (" ).append( ce.issueNo ).append( ")" ).append( systemNewLineChar );
        }
        return sb.toString();
    }

    static IndividualChangelog parse( final Path file )
        throws IOException
    {
        final String projectName;
        final String fileName = file.getFileName().toString().replaceFirst("[.][^.]+$", "");
        int versionLoc = fileName.lastIndexOf( "-v" );
        if ( versionLoc == -1 )
        {
            projectName = fileName.substring( fileName.indexOf( "_" ) + 1 );
        }
        else
        {
            projectName = fileName.substring( fileName.indexOf( "_" ) + 1, versionLoc );
        }

        final Map<String, List<ChangelogEntry>> entries = new HashMap<>();
        BufferedReader reader = new BufferedReader( new FileReader( file.toFile() ) );
        String text;
        ArrayList<ChangelogEntry> section = null;
        while ( ( text = reader.readLine() ) != null )
        {
            if ( text.startsWith( "##" ) )
            {
                section = new ArrayList<>();
                entries.put( text.substring( 3 ), section );
            }
            if ( text.startsWith( " - " ) )
            {
                if ( section == null )
                {
                    throw new IllegalStateException( "Found changelog entry outside of section in: " + file );
                }
                int titleEnd = text.indexOf( "(#" ) - 1;
                int issueNoStart = titleEnd + 2;
                int issueNoStop = text.indexOf( ")." );
                section.add( new ChangelogEntry( text.substring( 3, titleEnd ), text.substring( issueNoStart, issueNoStop ) ) );
            }
        }

        return new IndividualChangelog( projectName, entries );
    }

    private static class IndividualChangelog
    {
        final String project;

        final Map<String, List<ChangelogEntry>> entries;

        IndividualChangelog( final String project, final Map<String, List<ChangelogEntry>> entries )
        {
            this.project = project;
            this.entries = entries;
        }

        String getProject()
        {
            return project;
        }

        Map<String, List<ChangelogEntry>> getEntries()
        {
            return entries;
        }

    }

    private static class ChangelogEntry
    {
        final String description;

        final String issueNo;

        ChangelogEntry( final String description, final String issueNo )
        {
            this.description = description;
            this.issueNo = issueNo;
        }
    }

}
