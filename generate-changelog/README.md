# Generate-changelog

This tool generates the changelog for a Git repository between two Git references.
The changelog is based on the GitHub IDs defined in the Git Rev Commits.

## Building

Before trying to build the project, you need to verify that Java 11 installed.

Build all code:

    gradle installDist

## Usage

The script "generate-changelog" is generated in the sub-folder "build/install/generate-changelog/bin/"

When running "generate-changelog", the script is looking for `GITHUB_TOKEN` environment variable and optionally `GITHUB_ACTOR`.
These are used for logging in to GitHub. The GITHUB_TOKEN can be generated in GitHub by going to your setting/Personal Access Token.

    NAME
        generate-changelog - Generates the changelog

    SYNOPSIS
        generate-changelog [(-o <outputFiename>)] [(-h | --help)] [--ignore-field-check]
                [-p <gitDirectoryPath>] [-s <since>] [-u <until>]

    OPTIONS
        -o <outputFiename>
            Full path and file name of output. Default is changelog_<projectName>[-[<since>]..[<until>]].md in the current directory.

        -h, --help
            Display help information.

        --ignore-field-check
            Ignore the "Not In Changelog" field check.

        -p <gitDirectoryPath>
            Path of the Git repository (default value is the current directory).

        -s <since>
            Since the provided Git reference.

        -u <until>
            Until the provided Git reference.

Example:

    generate-changelog -p Workspace/git/xp -s v6.0.0 -u 0624445611a32e8cbed6aa71f8ac15c5b7d9af1a

## Output

The tool generates an MD file listing the GitHub issues contained in the Git commits.

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

    JAVA_OPTS=-Dlog4j.configuration=file:./log4j.properties generate-changelog -s master
