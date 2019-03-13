package com.enonic.xp.changelog;

public class ChangelogException
    extends Exception
{
    public ChangelogException( final String message )
    {
        super( message );
    }

    public ChangelogException( final String message, final Exception exception )
    {
        super( message, exception );
    }
}
