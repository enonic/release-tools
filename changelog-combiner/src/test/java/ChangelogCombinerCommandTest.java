import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;


class ChangelogCombinerCommandTest
{
    @Test
    void test(@TempDir Path tempDir) throws Exception
    {
        final Path resultFilePath = tempDir.resolve( "out.md" );
        ChangelogCombinerCommand.main( "-p", "test/input", "-o", resultFilePath.toString() , "changelog_*" );

        assertLinesMatch( Files.readAllLines( resultFilePath), Files.readAllLines( Path.of( "test/expected.md" ) ) );
    }
}
