package com.enonic.xp.changelog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class IndividualChangelog
{
    private String project;

    private HashMap<String, ArrayList<ChangelogEntry>> entries;

    private IndividualChangelog( final String project, final HashMap<String, ArrayList<ChangelogEntry>> entries )
    {
        this.project = project;
        this.entries = entries;
    }

    public String getProject()
    {
        return project;
    }

    public HashMap<String, ArrayList<ChangelogEntry>> getEntries()
    {
        return entries;
    }

    static IndividualChangelog parse( final String file )
        throws IOException, ChangelogException
    {
        int dotdotLoc = file.indexOf( ".." );
        int versionLoc = file.substring( 0, dotdotLoc ).lastIndexOf( "v" );
        String projectName = file.substring( 12, versionLoc-1 );
        IndividualChangelog ic = new IndividualChangelog( projectName, new HashMap<>(  ) );

        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        String text;
        ArrayList<ChangelogEntry> section = null;
        while ((text = reader.readLine()) != null) {
            if (text.startsWith( "##" )) {
                section = new ArrayList<ChangelogEntry>(  );
                ic.entries.put( text.substring( 3 ), section);
            }
            if (text.startsWith( " - " )) {
                if (section == null) {
                    throw new ChangelogException( "Found changelog entry outside of section in: " + file );
                }
                int titleEnd = text.indexOf( "(#" ) - 1;
                int issueNoStart = titleEnd + 3;
                int issueNoStop = text.indexOf( ")." );
                section.add( new ChangelogEntry( text.substring( 3, titleEnd ), Integer.parseInt( text.substring( issueNoStart, issueNoStop ) ) ) );
            }
        }

        return ic;
    }
}
