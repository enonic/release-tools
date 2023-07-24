import java.nio.file.Path;

import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.Placement;
import org.asciidoctor.SafeMode;
import org.asciidoctor.jruby.AsciiDocDirectoryWalker;
import org.asciidoctor.jruby.AsciidoctorJRuby;

public class DocGenerator
{
    public static void main( String[] args )
    {
        final Path sourceDir = args.length > 0 ? Path.of( args[0] ) : Path.of( "docs" );
        final var attributes = Attributes.builder()
            .backend( "html5" )
            .icons( "font" )
            .setAnchors( true )
            .attribute( "sectlinks", true )
            .attribute( "encoding", "utf-8" )
            .linkAttrs( true )
            .attribute( "idprefix", "" )
            .tableOfContents( Placement.RIGHT )
            .attribute( "outfilesuffix", ".ahtml" )
            .build();

        final Options options = Options.builder().backend( "html5" ).safe( SafeMode.UNSAFE ).attributes( attributes ).build();

        try (var asciidoctor = AsciidoctorJRuby.Factory.create())
        {
            asciidoctor.convertDirectory( new AsciiDocDirectoryWalker( sourceDir.toString() ), options );
        }
    }
}
