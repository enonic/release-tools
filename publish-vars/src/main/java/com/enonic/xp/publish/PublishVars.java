package com.enonic.xp.publish;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PublishVars
{
    public static String nextSnapshot( String currentVersion )
    {
        final Matcher matcher = Pattern.compile( "(\\d+\\.\\d+\\.)(\\d+)(-.*)?" ).matcher( currentVersion );
        if ( matcher.matches() )
        {
            final String mainPart = matcher.group( 1 );
            final String currentPatchVersion = matcher.group( 2 );
            final boolean noSuffix = matcher.group( 3 ) == null;
            final String patchVersion = noSuffix ? String.valueOf( Integer.parseInt( currentPatchVersion ) + 1 ) : currentPatchVersion;
            return mainPart + patchVersion + "-SNAPSHOT";
        }
        else
        {
            throw new IllegalArgumentException( "Invalid version format " + currentVersion );
        }
    }

    public static void main( String[] args )
        throws Exception
    {
        final String file = args[0];
        final Properties properties = new Properties();
        try (final FileReader reader = new FileReader( file, StandardCharsets.UTF_8 ))
        {
            properties.load( reader );
        }

        final String version = properties.getProperty( "version" );
        final String projectName = properties.getProperty( "projectName" );

        final boolean isSnapshot = version.endsWith( "-SNAPSHOT" );
        System.out.println( "::set-output name=nextSnapshot::" + nextSnapshot( version ) );
        System.out.println( "::set-output name=repo::" + ( isSnapshot ? "snapshot" : "public" ) );
        System.out.println( "::set-output name=release::" + !isSnapshot );
        System.out.println( "::set-output name=prerelease::" + version.contains( "-" ) );
        System.out.println( "::set-output name=tag_name::" + "v" + version );
        System.out.println( "::set-output name=version::" + version );
        if ( projectName != null )
        {
            System.out.println( "::set-output name=projectName::" + projectName );
        }
    }
}
