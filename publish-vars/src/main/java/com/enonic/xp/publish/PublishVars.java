package com.enonic.xp.publish;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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

    public static String adjustedRepoKey( String repoKey, boolean isPrivateCodeRepo, boolean isSnapshot )
    {
        String repoKeyOrDefault = Objects.requireNonNullElse( repoKey, isPrivateCodeRepo ? "inhouse" : "public" );

        return ( repoKeyOrDefault.equals( "public" ) && isSnapshot ) ? "snapshot" : repoKeyOrDefault;
    }

    public static void main( String[] args )
        throws Exception
    {
        final String propertiesPath = System.getenv( "PROPERTIES_PATH" );
        final String githubRepoPrivate = System.getenv( "GITHUB_REPO_PRIVATE" );

        final Properties properties = new Properties();
        try (final FileReader reader = new FileReader( propertiesPath, StandardCharsets.UTF_8 ))
        {
            properties.load( reader );
        }

        final String version = properties.getProperty( "version" );
        if ( version == null )
        {
            return;
        }
        final String projectName = properties.getProperty( "projectName" );
        final String repoKey = properties.getProperty( "repoKey" );
        final boolean isSnapshot = version.endsWith( "-SNAPSHOT" );

        System.out.println( "::set-output name=nextSnapshot::" + nextSnapshot( version ) );
        System.out.println( "::set-output name=repo::" + adjustedRepoKey( repoKey, "true".equals( githubRepoPrivate ), isSnapshot ) );
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
