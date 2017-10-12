package com.enonic.xp.release.phrases;

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

    @Inject
    public HelpOption helpOption;

    @Option(name = "-p", description = "Path of the directory containing the phrases files")
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
            LOGGER.error( "Error while generating the change log: " + e.getMessage() );
            LOGGER.debug( "Error details: ", e );
        }
    }

    private void run()
    {
        System.exit( 1 );
    }
}