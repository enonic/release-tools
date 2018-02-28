# Validate-phrases

This tools displays the list of missing keys for phrases files

## Building

Before trying to build the project, you need to verify that the following software are installed:

* Java 8 (update 40 or above) for building and running.
* Gradle 2.x build system.

Build all code:

    gradle installDist
    
## Usage

The script "validate-phrases" is generated in the sub-folder "build/install/validate-phrases/bin/"

    NAME
            validate-phrases - Validates phrases properties files
    
    SYNOPSIS
            validate-phrases [(-h | --help)] [-p <path>]
    
    OPTIONS
            -h, --help
                Display help information
    
            -p <path>
                Path of the directory containing the phrases files
                
            -f <filename>
                Filename if not "phrases.properties"
            
Example:
```
GIT_REPO_PATH=/Users/gri/Workspace/git
validate-phrases -p $GIT_REPO_PATH/lib-admin-ui/src/main/resources/i18n -f common.properties
validate-phrases -p $GIT_REPO_PATH/xp-apps/modules/app-applications/src/main/resources/i18n
validate-phrases -p $GIT_REPO_PATH/xp-apps/modules/app-contentstudio/src/main/resources/i18n
validate-phrases -p $GIT_REPO_PATH/xp-apps/modules/app-users/src/main/resources/i18n
validate-phrases -p $GIT_REPO_PATH/app-standardidprovider/src/main/resources/i18n
```
    
