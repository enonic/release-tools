## GitHub Search API

This script allows you to use Github Search API for Enonic organization.

# Usage

## Install dependencies

    npm install

## Fetch repositories

To fetch repositories by specific criteria use the following command:

    node github.js find <token> <repoNameRegExp> [fileLocation] [contentRegExp] [--options]

**List of arguments:**

- `token` - The personal access token. In order to set it up to visit this [page](https://github.com/settings/tokens).
- `repoNameRegExp` - RegExp pattern for a repository name.
- `fileLocation` - File location.
- `contentRegExp` - RegExp pattern for a content.

**List of options:**

- `not-contains-file` - If this option is specified then result will contain repository names which don't have a given file
- `public` - Include only public repositories that you can access
- `private` - Include only private repositories that you can access
- `not-archived` - Include only repositories that are not archived
- `archived` - Include only repositories that are archived
- `not-mirror` - Include only repositories that are not mirrors
- `mirror` Include only repositories that are mirrors
- `fork` - Include only repositories that are forks

## Examples

To fetch all repositories where gradle.properties file does not contain a `version` property with SNAPSHOT suffix:

```
node github.js find <your_personal_token> "^app-" "gradle.properties" '^version=.*(?<!SNAPSHOT)$'
```

Find all repositories without dependabot integration

```
node github.js find <your_personal_token> ".*" ".github/dependabot.yml" --not-contains-file --not-archived
```
