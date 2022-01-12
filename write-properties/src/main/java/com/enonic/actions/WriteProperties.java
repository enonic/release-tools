package com.enonic.xp.publish;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class WriteProperties
{
    static final Pattern PATTERN = Pattern.compile( "^(?<prefix>^(?<key>[^ ]+) *[=:] *)(?<value>.+)$" );

    public static void main( String[] args )
        throws Exception
    {
        final String propertiesPath = args[0];
        final String property = args[1];
        final String value = args[2];

        String lineSeparator = guessLineSeparator( Files.readAllBytes( Path.of( propertiesPath ) ) );

        AtomicBoolean updated = new AtomicBoolean();
        final StringBuilder sb = new StringBuilder();
        try (Stream<String> lines = Files.lines( Path.of( propertiesPath ) ))
        {
            lines.forEachOrdered( line -> {
                final Matcher matcher = PATTERN.matcher( line );

                if ( matcher.matches() && matcher.group( "key" ).equals( property ) && !matcher.group( "value" ).equals( value ) )
                {
                    sb.append( matcher.group( "prefix" ) ).append( value ).append( lineSeparator );
                    updated.set( true );
                }
                else
                {
                    sb.append( line ).append( lineSeparator );
                }
            } );
        }
        if ( updated.get() )
        {
            Files.writeString( Path.of( propertiesPath ), sb.toString() );
        }
    }

    public static String guessLineSeparator( final byte[] outerArray )
    {
        for ( int i = 0; i < outerArray.length - 1; i++ )
        {
            if ( outerArray[i] == '\r' && outerArray[i] == '\n' )
            {
                return "\r\n";
            }
        }
        return System.lineSeparator();
    }
}
