const {request} = require("@octokit/request");
const yargs = require('yargs');
const fs = require('fs');

const argv = yargs
    .command('run <token> <file> <location> <repo>', 'Copies the given file to the specified repositories.', (yargs) => {
        yargs.positional('token', {
            describe: 'The personal access token. In order to set it up to visit https://github.com/settings/tokens page.',
            type: 'string'
        }).positional('file', {
            describe: 'The absolute path to a file',
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

function doExecute() {
    let location = argv.location;
    let repositories = argv.repo.split(' ');

    if (repositories.length === 0) {
        console.error("Error: The \"repo\" parameter can not be empty.");
        return;
    }

    fs.readFile(argv.file, 'utf8', async (err, content) => {
            if (err) {
                console.error(err);
                return;
            }

            for (const repository of repositories) {
                let url = `/repos/enonic/${repository}/contents/${location}`;

                let sha = await request({
                    method: 'GET',
                    headers: {
                        Authorization: `token ${argv.token}`
                    },
                    url: url
                }).then(res => {
                    return res.data.sha;
                }).catch(err => {
                    if (err.status === 404) {
                        console.warn(`${url} not found`);
                        return null
                    } else {
                        throw err
                    }
                });

                let data = {
                    content: Buffer.from(content).toString('base64'),
                    message: `Updated ${location} file`
                };

                if (sha !== null) {
                    data.sha = sha;
                }

                await request({
                    headers: {
                        Authorization: `token ${argv.token}`
                    },
                    method: 'PUT',
                    url: url,
                    data: data
                }).then((res, err) => {
                    console.log(`The repository "${repository}" successfully updated.`);
                }).catch(err => {
                    console.error(`Error: Update of the repository "${repository}" is failed.\n ${err}`);
                });
            }
        }
    );
}

function execute() {
    if (argv._.includes('run')) {
        doExecute();
    } else {
        console.error('Error: Command is not specified. Use --help to know more details.');
    }
}

execute();
