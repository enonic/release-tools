# Generate-changelog

This tools generates the changelog for a Git repository between two Git references.
The changelog is based on the YouTrack IDs defined in the Git Rev Commits

## Building

Before trying to build the project, you need to verify that the following software are installed:

* Java 8 (update 40 or above) for building and running.
* Gradle 2.x build system.

Build all code:

    gradle installDist

## Usage

The script "generate-changelog" is generated in the sub-folder "build/install/generate-changelog/bin/"

    NAME
        generate-changelog - Generates the changelog

    SYNOPSIS
        generate-changelog [(-h | --help)] [--ignore-field-check]
                [-p <gitDirectoryPath>] [-s <since>] [-u <until>]

    OPTIONS
        -h, --help
            Display help information

        --ignore-field-check
            Ignore the YouTrack Changelog field check.

        -p <gitDirectoryPath>
            Path of the Git repository (default value is .git).

        -s <since>
            Since the provided Git reference.

        -u <until>
            Until the provided Git reference.

Example

    generate-changelog -p Workspace/git/xp/.git -s v6.0.0 -u 0624445611a32e8cbed6aa71f8ac15c5b7d9af1a

## Output

The tool generates a MD file "changelog[-[since]..[until]].md", in the current directory, listing the YouTrack issues contained in the Git commits