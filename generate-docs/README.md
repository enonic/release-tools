Enonic documentation is written in asciidoc and published to https://developer.enonic.com/docs

## Initiate new documentation

- Create `/docs` directory in the repository and add `index.adoc` file
- Add a new Github Actions workflow "Enonic Documentation"
  - Follow [Github Documentation](https://docs.github.com/en/actions/using-workflows/using-starter-workflows#using-starter-workflows) to add "Enonic Documentation" workflow. 
  - Actual Template file can be found [here](https://github.com/enonic/.github/blob/master/workflow-templates/enonic-docgen.yml).
  - In case you need multiple documenation branches add `/docs/versions.json` to your default repository branch.

## How it works

Workflow script does:
- checkout default repository branch to lookup `versions.json` file.
- checks out every commit/branch/tag in `versions` list
  - converts `*.adoc` files into `*.ahtml` files found in `/docs` directory
  - copies the results to a output directory "$output/$label"
  - copies/generates `versions.json` to the root of output directory 
- copies the output into `gh-pages` branch of the repository
- notifies developer.ennoic.com about changes in `gh-pages` branch available for download

For example, if default branch contains `/docs/versions.json` file:
```
{
  "versions": [
    {
      "label": "2.x",
      "checkout": "a24d80d3eeef3439ff9f4f192bc3a567da2493f1"
    },
    {
      "label": "3.x",
      "checkout": "aa436bce3e13156cb4c09fcdabe900ee7da584f3"
    },
    {
      "label": "4.x",
      "checkout": "9e9a086"
    },
    {
      "label": "stable",
      "checkout": "5.0",
      "latest": true
    }      
  ]
}
```
will generate 4 directories with documentation called `2.x`, `3.x`, `4.x`, `stable`. 
`2.x`, `3.x`, `4.x` directories correcpond to a specific commit, while `stable` directory will contain results generated from the HEAD of `5.0` branch

If there is no `/docs/versions.json` in the default branch, a substitute one is generated
```
  "versions": [
    {
      "label": "stable",
      "checkout": "$default-branch-name",
      "latest" : true
    }      
  ]
```

The importing is done by developer.enonic.com when the webhook notification is received. Webhook is protected by HMAC-SHA256 signature sent via `x-hub-signature-256` header.
