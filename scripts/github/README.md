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

Find version of gradle in all repositories (only include Gradle versions 6.x )

```
node github.js find <your_personal_token> ".*" gradle/wrapper/gradle-wrapper.properties "gradle-(?<out>6.+)-.*" --not-archived
```
Find all repositories without dependabot integration

```
node github.js find <your_personal_token> ".*" ".github/dependabot.yml" --not-contains-file --not-archived
```

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


## GitHub Dependabot update schedule interval API:

**List of arguments:**

- `token` - The personal access token. In order to set it up to visit this [page](https://github.com/settings/tokens).
- `repoNameRegExp` - RegExp pattern for a repository name.
- `packageEcosystem` - Package Ecosystem, for instance gradle, npm and etc
- `interval` - Schedule interval weekly|monthly etc
- `repo` -  Name of the repository for which the changes are applied. Use space between repositories to specify multiple.

**Examples**

```
 node update-interval.js run <token> npm <interval> <repo>
```


## GitHub Gradle version API:

In order to know current version of gradle for repository you can call the following command:

```
  node gradle.js run <token> "<repoNameRegExp>"

```

**List of arguments:**

- `token` - The personal access token. In order to set it up to visit this [page](https://github.com/settings/tokens).
- `repoNameRegExp` - RegExp pattern for a repository name.

In the result you will see something like that:

```
xp-distro 6.2.1
app-poll 5.4.1
```

## GitHub Commit API:

In order to commit files you can use the following command:

```
node commit.js commit <token> <repos> <dir> <comment>
```
**List of arguments:**

- `token` - The personal access token. In order to set it up to visit this [page](https://github.com/settings/tokens).
- `repos` - Repository names space separated.
- `dir` - Path to directory with files for commit.
- `comment` - Comment for GitHub commit.
