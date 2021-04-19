package com.enonic.xp.publish;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PublishVarsTest
{
    @Test
    void calc()
    {
        assertAll( () -> assertEquals( "3.0.9-SNAPSHOT", PublishVars.nextSnapshot( "3.0.8" ) ),
                   () -> assertEquals( "3.0.8-SNAPSHOT", PublishVars.nextSnapshot( "3.0.8-SNAPSHOT" ) ),
                   () -> assertEquals( "1.11.0-SNAPSHOT", PublishVars.nextSnapshot( "1.11.0-SNAPSHOT" ) ),
                   () -> assertEquals( "3.0.8-SNAPSHOT", PublishVars.nextSnapshot( "3.0.8-RC1" ) ),
                   () -> assertEquals( "3.0.8-SNAPSHOT", PublishVars.nextSnapshot( "3.0.8-BETA1" ) ) );
    }
}
