## Search API

This script allows you to use Github Search API for Enonic organization.

# Usage

## Fetch repositories

To fetch repositories by specific criteria use the following command:

    node find-file.js find <token> <fileLocation> <repoNameRegExp> [contentRegExp] [--options]

**List of arguments:**

- `token` - The personal access token. In order to set it up to visit this [page](https://github.com/settings/tokens).
- `fileLocation` - File location.
- `repoNameRegExp` - RegExp pattern for a repository name.
- `contentRegExp` - RegExp pattern for a content.

**List of options:**

- `not-contains-file` - If this option is specified then result will contain repository names which don't have a given file
- `public` - Includes public repositories that you can access
- `private` - Includes private repositories that you can access
- `not-archived` - Includes repositories that are not archived
- `archived` - Includes repositories that are archived
- `not-mirror` - Includes repositories that are not mirrors
- `mirror` Includes repositories that are mirrors
- `fork` - Includes repositories that are forks

## Examples

To fetch all repositories where gradle.properties file does not contain a `version` property with SNAPSHOT suffix:

```
node find-file.js find <your_personal_token> "gradle.properties" "^app-" '^version=.*(?<!SNAPSHOT)$'
```
 
