package com.enonic.xp.publish;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublishVarsTest
{
    @Test
    void nextSnapshot()
    {
        assertAll( () -> assertEquals( "3.0.9-SNAPSHOT", PublishVars.nextSnapshot( "3.0.8" ) ),
                   () -> assertEquals( "3.0.8-SNAPSHOT", PublishVars.nextSnapshot( "3.0.8-SNAPSHOT" ) ),
                   () -> assertEquals( "1.11.0-SNAPSHOT", PublishVars.nextSnapshot( "1.11.0-SNAPSHOT" ) ),
                   () -> assertEquals( "3.0.8-SNAPSHOT", PublishVars.nextSnapshot( "3.0.8-RC1" ) ),
                   () -> assertEquals( "3.0.8-SNAPSHOT", PublishVars.nextSnapshot( "3.0.8-BETA1" ) ) );
    }

    @Test
    void isSnapshot()
    {
        assertAll( () -> assertTrue( PublishVars.isSnapshot( "8.1.0-SNAPSHOT" ) ),
                   () -> assertTrue( PublishVars.isSnapshot( "8.1.0-SNAPSHOT " ) ),
                   () -> assertFalse( PublishVars.isSnapshot( "8.0.0" ) ),
                   () -> assertFalse( PublishVars.isSnapshot( "8.1.0-RC2" ) ),
                   () -> assertFalse( PublishVars.isSnapshot( null ) ) );
    }

    @Test
    void adjustedRepoKey()
    {
        assertAll( () -> assertEquals( "public", PublishVars.adjustedRepoKey( "public", false, false ) ),
                   () -> assertEquals( "snapshot", PublishVars.adjustedRepoKey( "public", true, true ) ),
                   () -> assertEquals( "public", PublishVars.adjustedRepoKey( "public", true, false ) ),
                   () -> assertEquals( "snapshot", PublishVars.adjustedRepoKey( "public", false, true ) ),

                   () -> assertEquals( "other", PublishVars.adjustedRepoKey( "other", false, false ) ),
                   () -> assertEquals( "other", PublishVars.adjustedRepoKey( "other", true, true ) ),
                   () -> assertEquals( "other", PublishVars.adjustedRepoKey( "other", true, false ) ),
                   () -> assertEquals( "other", PublishVars.adjustedRepoKey( "other", false, false ) ),

                   () -> assertEquals( "public", PublishVars.adjustedRepoKey( null, false, false ) ),
                   () -> assertEquals( "", PublishVars.adjustedRepoKey( null, true, true ) ),
                   () -> assertEquals( "", PublishVars.adjustedRepoKey( null, true, false ) ),
                   () -> assertEquals( "snapshot", PublishVars.adjustedRepoKey( null, false, true ) ) );
    }
}
