const {request} = require("@octokit/request");
const yargs = require('yargs');
const fs = require('fs');

const argv = yargs
    .command('run <token> <location> <repo>', 'Copies the given file to the specified repositories.', (yargs) => {
        yargs.positional('token', {
            describe: 'The personal access token. In order to set it up to visit https://github.com/settings/tokens page.',
            type: 'string'
        }).positional('location', {
            describe: 'The relative destination path',
            type: 'string'
        }).positional('repo', {
            describe: 'The name of the repository for which the changes are applied. If you want to specify more than one repository use space between repository names.',
            type: 'string'
        })
    })
    .help()
    .alias('help', 'h')
    .argv;

async function doExecute() {
    let location = argv.location;
    let repositories = argv.repo.split(' ');

    if (repositories.length === 0) {
        console.error("Error: The \"repo\" parameter can not be empty.");
        return;
    }
            for (const repository of repositories) {
                let url = `/repos/enonic/${repository}/contents/${location}`;
                //console.log(`${repository} ${url}`);
                await request({
                    method: 'GET',
                    headers: {
                        Authorization: `token ${argv.token}`
                        , Accept: 'application/vnd.github.v3.raw'
                    },
                    url: url
                }).then(res => {
                    console.log(repository)
                    console.log(res.data)
                }).catch(err => {
                    if (err.status === 404) {
                      console.warn(`not found in ${repository}`);
                    } else {
                        console.error(err);
                    }
                    return null
                });
            }
}

function execute() {
    if (argv._.includes('run')) {
        doExecute();
    } else {
        console.error('Error: Command is not specified. Use --help to know more details.');
    }
}

execute();
