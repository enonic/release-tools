# GitHub Update API

This script allows you to copy a local file to one or multiple repositories.

# Usage

## Update file in repositories

To fetch repositories by specific criteria use the following command:

    node github-update.js run <token> <file> <location> <repo>

**List of arguments:**

- `token` - Personal access token. In order to set it up to visit this [page](https://github.com/settings/tokens).
- `file` -  Path to a local file with content.
- `location` - Relative destination path.
- `repo` -  Name of the repository for which the changes are applied. Use space between repositories to specify multiple.

**_Note:_** All arguments are required.


# Example

You can copy a custom configuration file for `Dependabot` with the following command:

    node github-update.js run <token> dependabot-template.yml .github/dependabot.yml <repo>

The template you can find [here](https://github.com/enonic/release-tools/blob/master/scripts/dependabot/dependabot-template.yml).
