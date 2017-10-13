package com.enonic.xp.release.phrases;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
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

    private static final Pattern PHRASES_FILE_NAME_PATTERN = Pattern.compile( "phrases_([^\\.]+).properties" );

    @Inject
    public HelpOption helpOption;

    @Option(name = "-p", description = "Path of the directory containing the phrases files", required = true)
    public String path;


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
        File directory = new File( this.path );
        if ( !directory.isDirectory() )
        {
            LOGGER.error( "'" + directory.getAbsolutePath() + "' is not a directory." );
            return;
        }

        final File defaultPhraseFile = new File( directory, DEFAULT_PHRASE_FILE );
        if ( !defaultPhraseFile.isFile() )
        {
            LOGGER.error( "'" + directory.getAbsolutePath() + "' does not contain any file '" + DEFAULT_PHRASE_FILE + "'" );
            return;
        }

        final Set<String> defaultKeys = getPropertyKeySet( defaultPhraseFile.toPath() );

        Files.list( directory.toPath() ).
            forEach( filePath -> {
                final Matcher matcher = PHRASES_FILE_NAME_PATTERN.matcher( filePath.getFileName().toString() );
                if ( matcher.matches() )
                {
                    final String language = matcher.group( 1 );
                    final Set<String> propertyKeySet = getPropertyKeySet( filePath );
                    final Set<String> missingKeysSet =
                        defaultKeys.stream().filter( defaultKey -> !propertyKeySet.contains( defaultKey ) ).collect( Collectors.toSet() );
                    if ( !missingKeysSet.isEmpty() )
                    {
                        LOGGER.info( "The following keys are missing in '" + filePath.getFileName() + "': " + missingKeysSet );
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
            throw new RuntimeException( e );
        }
    }
}