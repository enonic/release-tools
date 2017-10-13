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
            
Example:
```
XP_APP_REPO_PATH=/Users/gri/Workspace/git/xp-apps
validate-phrases -p $XP_APP_REPO_PATH/modules/app-applications/src/main/resources/admin/i18n
validate-phrases -p $XP_APP_REPO_PATH/modules/app-contentstudio/src/main/resources/admin/i18n
validate-phrases -p $XP_APP_REPO_PATH/modules/app-standardidprovider/src/main/resources/admin/i18n
validate-phrases -p $XP_APP_REPO_PATH/modules/app-users/src/main/resources/admin/i18n
```
    
