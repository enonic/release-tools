package com.enonic.xp.changelog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.io.Files;

class IndividualChangelog
{
    private String project;

    private HashMap<String, ArrayList<ChangelogEntry>> entries;

    IndividualChangelog( final String project, final HashMap<String, ArrayList<ChangelogEntry>> entries )
    {
        this.project = project;
        this.entries = entries;
    }

    String getProject()
    {
        return project;
    }

    HashMap<String, ArrayList<ChangelogEntry>> getEntries()
    {
        return entries;
    }

    static IndividualChangelog parse( final Path file )
        throws IOException, ChangelogException
    {
        String projectName;
        final String fileName = Files.getNameWithoutExtension( file.getFileName().toString() );
        int versionLoc = fileName.lastIndexOf( "-v" );
        if ( versionLoc == -1 )
        {
            projectName = fileName.substring( fileName.indexOf( "_" ) + 1 );
        }
        else
        {
            projectName = fileName.substring( fileName.indexOf( "_" ) + 1, versionLoc );
        }
        IndividualChangelog ic = new IndividualChangelog( projectName, new HashMap<>() );

        BufferedReader reader = new BufferedReader( new FileReader( file.toFile() ) );
        String text;
        ArrayList<ChangelogEntry> section = null;
        while ( ( text = reader.readLine() ) != null )
        {
            if ( text.startsWith( "##" ) )
            {
                section = new ArrayList<>();
                ic.entries.put( text.substring( 3 ), section );
            }
            if ( text.startsWith( " - " ) )
            {
                if ( section == null )
                {
                    throw new ChangelogException( "Found changelog entry outside of section in: " + file );
                }
                int titleEnd = text.indexOf( "(#" ) - 1;
                int issueNoStart = titleEnd + 2;
                int issueNoStop = text.indexOf( ")." );
                section.add( new ChangelogEntry( text.substring( 3, titleEnd ), text.substring( issueNoStart, issueNoStop ) ) );
            }
        }

        return ic;
    }
}
