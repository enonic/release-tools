# Changelog-combiner

This tools takes the changelogs created by `generate-changelog`, combines them into one list, so that the customer do not
have to know the internal structure of XP to read the changelog.

## Building

Before trying to build the project, you need to verify that the following software are installed.
The provided Gradle wrapper may be used:

* Java 8 (update 40 or above) for building and running.
* Gradle 2.x build system.

Build all code:

    ./gradle installDist

## Usage

The script "changelog-combiner" is generated in the sub-folder "build/install/changelog-combiner/bin/"

    NAME
        changelog-combiner - Combines changelogs generated by the generate-changelog tool.

    SYNOPSIS
        changelog-combiner [(-w <ownerOrganization>)] [(-w <ownerOrganization>)]  [(-h | --help)] <changelogFiles>...

    OPTIONS
        -o <outputFile>
            Output file.

        -w <ownerOrganization>
            Default is enonic.

        -h, --help
            Display help information

Example:

    generate-changelog -o changelog-combined.md /path/to/changlog/changelog*.md

## Output

The tool generates a MD file "changelog.md", in the current directory, listing the GitHub issues combined

## Verbosity

More information about the internal behaviour of the tool can be displayed on the standard output

Create a file "log4j.properties" containing the following configuration

    log4j.rootLogger=INFO, A1
    log4j.appender.A1=org.apache.log4j.ConsoleAppender
    log4j.appender.A1.layout=org.apache.log4j.PatternLayout
    log4j.appender.A1.layout.ConversionPattern=%m%n

    log4j.logger.com.enonic.xp.changelog=DEBUG, A2
    log4j.additivity.com.enonic.xp.changelog=false
    log4j.appender.A2=org.apache.log4j.ConsoleAppender
    log4j.appender.A2.layout=org.apache.log4j.PatternLayout
    log4j.appender.A2.layout.ConversionPattern=%-4r [%t] %-5p %c %x - %m%n

Set the path of this file as a system property "log4j.configuration"

Example:

    JAVA_OPTS=-Dlog4j.configuration=file:./log4j.properties generate-changelog -o changelog-combined.md /path/to/changlog/changelog*.md
