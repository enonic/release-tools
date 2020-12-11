# Dependabot for Enonic XP, Apps and Libs

This script allows you to copy the `dependabot` configuration file to `.github` folder for all specified repositories.

The template you can find [here](https://github.com/enonic/release-tools/blob/master/scripts/dependabot/dependabot-template.yml).

# Usage

You can copy a custom configuration file for `Dependabot` with the following command:

    node dependabot.js run <token> <file> <repo>
    
**List of arguments:**

- `token` - The personal access token. In order to set it up to visit this [page](https://github.com/settings/tokens).
- `file` -  The absolute path to a file.
- `repo` -  The name of the repository for which the changes are applied. If you want to specify more than one repository use space between repository.

**_Note:_** All arguments are required.
