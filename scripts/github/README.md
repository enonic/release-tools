## GitHub Search API

This script allows you to use Github Search API for Enonic organization.

# Usage

## Fetch repositories

To fetch repositories by specific criteria use the following command:

    node github.js repos <token> [name] [delimiter] [--options]

**List of arguments:**

- `token` - The personal access token. In order to set it up to visit this [page](https://github.com/settings/tokens).
- `name` - The RegExp pattern for a repository name.
- `delimiter` - The repository names in a result will be separated by a specified delimiter. The default delimiter is comma `\n` (new line).

**List of options:**

- `public` - Includes public repositories that you can access
- `private` - Includes private repositories that you can access
- `not-archived` - Includes repositories that are not archived
- `archived` - Includes repositories that are archived
- `not-mirror` - Includes repositories that are not mirrors
- `mirror` Includes repositories that are mirrors
- `fork` - Includes repositories that are forks

For example, if you want to fetch all repositories which name starts from `cms` and are private use the following command:

    node github.js repos <your_personal_token> ^cms --private
