package com.enonic.xp.release.phrases;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

@Command(name = "validate-phrases", description = "Validates phrases properties files")
public class ValidatePhrasesCommand
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ValidatePhrasesCommand.class );

    private static final String DEFAULT_PHRASE_FILE = "phrases.properties";

    private static final Pattern PROPERTY_KEY_PATTERN = Pattern.compile( "^\\s*([\\S^#]+)\\s*=" );

    private static final String PHRASES_FILE_NAME_PATTERN_ENDING = "_([^\\.]+).properties";

    @Inject
    public HelpOption helpOption;

    @Option(name = "-p", description = "Path of the directory containing the phrases files")
    public String path;

    @Option(name = "-f", description = "Name of phrases properties-file.  Default is 'phrases.properties'")
    public String fileName;

    public Pattern phrasesFileNamePattern;


    public static void main( String[] args )
    {
        try
        {
            ValidatePhrasesCommand generateChangelogCommand = SingleCommand.singleCommand( ValidatePhrasesCommand.class ).parse( args );

            if ( generateChangelogCommand.helpOption.showHelpIfRequested() )
            {
                return;
            }

            generateChangelogCommand.run();
        }
        catch ( Exception e )
        {
            LOGGER.error( "Error while validation the phrases files: " + e.getMessage() );
            LOGGER.debug( "Error details: ", e );
        }
    }

    private void run()
        throws IOException
    {
        if ( this.path == null )
        {
            throw new RuntimeException( "Required option '-p' is missing" );
        }

        if ( this.fileName == null )
        {
            fileName = DEFAULT_PHRASE_FILE;
        }

        if ( !this.fileName.endsWith( ".properties" ) )
        {
            LOGGER.error( "The file with phrases must be a '.properties'-file." );
            return;
        }

        String mainFileName = fileName.substring( 0, fileName.length() - ".properties".length() );

        phrasesFileNamePattern = Pattern.compile( mainFileName + PHRASES_FILE_NAME_PATTERN_ENDING );

        File directory = new File( this.path );
        if ( !directory.isDirectory() )
        {
            LOGGER.error( "'" + directory.getAbsolutePath() + "' is not a directory." );
            return;
        }

        final File defaultPhraseFile = new File( directory, fileName );
        if ( !defaultPhraseFile.isFile() )
        {
            LOGGER.error( "'" + directory.getAbsolutePath() + "' does not contain any file '" + fileName + "'" );
            return;
        }

        final Set<String> defaultKeys = getPropertyKeySet( defaultPhraseFile.toPath() );

        Files.list( directory.toPath() ).
            forEach( filePath -> {
                final Matcher matcher = phrasesFileNamePattern.matcher( filePath.getFileName().toString() );
                if ( matcher.matches() )
                {
                    final Set<String> propertyKeySet = getPropertyKeySet( filePath );

                    final Set<String> missingKeysSet = new TreeSet(
                        defaultKeys.stream().filter( defaultKey -> !propertyKeySet.contains( defaultKey ) ).collect( Collectors.toSet() ) );

                    final Set<String> removedKeysSet = new TreeSet(
                        propertyKeySet.stream().filter( removedKey -> !defaultKeys.contains( removedKey ) ).collect( Collectors.toSet() ) );

                    if ( !missingKeysSet.isEmpty() )
                    {
                        LOGGER.info( "The following keys are missing in '" + filePath.getFileName() + "': " );
                        missingKeysSet.stream().forEach( missingKey -> LOGGER.info( " - " + missingKey ) );
                    }
                    if ( !removedKeysSet.isEmpty() )
                    {
                        LOGGER.info( "The following keys are in '" + filePath.getFileName() + "' but not in source file: " );
                        removedKeysSet.stream().forEach( removedKey -> LOGGER.info( " -" + removedKey ) );

                    }
                }
            } );
    }

    private Set<String> getPropertyKeySet( final Path filePath )
    {
        try
        {
            return Files.readAllLines( filePath ).
                stream().
                map( line -> {
                    final Matcher matcher = PROPERTY_KEY_PATTERN.matcher( line );
                    if ( matcher.find() )
                    {
                        return matcher.group( 1 );
                    }
                    return null;
                } ).
                filter( Objects::nonNull ).
                collect( Collectors.toSet() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Error reading file: " + filePath, e );
        }
    }
}