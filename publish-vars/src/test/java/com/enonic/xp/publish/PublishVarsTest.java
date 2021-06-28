package com.enonic.xp.publish;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
