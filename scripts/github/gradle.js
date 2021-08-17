const yargs = require('yargs');
const utils = require('./utils');

const argv = yargs.command('run <token> <repoNameRegExp>',
    'Description ...',
    (yargs) => {
        yargs.positional('token', {
            describe: 'The personal access token. In order to set it up to visit https://github.com/settings/tokens page.',
            type: 'string'
        }).positional('repoNameRegExp', {
            describe: 'RegExp pattern for a repository name.',
            type: 'string'
        })
    }, async (argv) => {
        let owner = 'enonic';
        let fileLocation = 'gradle/wrapper/gradle-wrapper.properties';

        let repositories = await utils.fetchRepos(owner, argv.token, 100, 1);

        let repositoryNames = repositories.filter(repository => doFilterRepo(repository, argv)).map(repository => {
            return repository.name;
        });

        for (const repositoryName of repositoryNames) {
            await readFile(owner, repositoryName, fileLocation, argv);
        }
    })
    .option('public', {
        type: 'boolean',
        description: 'Includes public repositories that you can access'
    })
    .option('private', {
        type: 'boolean',
        description: 'Includes private repositories that you can access'
    })
    .option('not-archived', {
        type: 'boolean',
        description: 'Includes repositories that are not archived'
    })
    .option('archived', {
        type: 'boolean',
        description: 'Includes repositories that are archived'
    })
    .option('not-mirror', {
        type: 'boolean',
        description: 'Includes repositories that are not mirrors'
    })
    .option('mirror', {
        type: 'boolean',
        description: 'Includes repositories that are mirrors'
    })
    .option('fork', {
        type: 'boolean',
        description: 'Includes repositories that are forks'
    })
    .help()
    .alias('help', 'h')
    .argv;

function doFilterRepo(repository, argv) {
    let argumentsCount = 0;
    let matchesCount = 0;

    if (argv.repoNameRegExp && argv.repoNameRegExp.length > 0) {
        let repoNameMatched = new RegExp(argv.repoNameRegExp).test(repository.name);
        if (!repoNameMatched) {
            return false;
        }
    }

    if (argv.public) {
        argumentsCount++;
        if (repository.private === false) {
            matchesCount++;
        }
    }

    if (argv.private) {
        argumentsCount++;
        if (repository.private === true) {
            matchesCount++;
        }
    }
    if (argv['not-archived']) {
        argumentsCount++;
        if (repository.archived === false) {
            matchesCount++;
        }
    }
    if (argv.archived) {
        argumentsCount++;
        if (repository.archived === true) {
            matchesCount++;
        }
    }
    if (argv.fork) {
        argumentsCount++;
        if (repository.fork === true) {
            matchesCount++;
        }
    }
    if (argv.mirror) {
        argumentsCount++;
        if (repository.mirror_url !== null) {
            matchesCount++;
        }
    }
    if (argv['not-mirror']) {
        argumentsCount++;
        if (repository.mirror_url === null) {
            matchesCount++;
        }
    }

    return argumentsCount === matchesCount;
}

async function readFile(owner, repository, fileLocation, argv) {
    try {
        let response = await utils.readFile(argv.token, owner, repository, fileLocation);

        if (response.status === 200) {
            let content = Buffer.from(response.data.content, 'base64').toString('utf8');

            let lines = content.split('\n');
            let regexp = /^gradle-?(\d+(?:\.\d+)*)-bin.zip$/;
            for (const line of lines) {
                let trimmedLine = line.trim();
                if (trimmedLine.startsWith('distributionUrl')) {
                    let paths = trimmedLine.split('/');
                    let zipName = paths[paths.length - 1];
                    let matched = zipName.match(regexp) || [];
                    if (matched.length >= 2) {
                        console.log(`${repository} ${matched[1]}`);
                    }
                }
            }
        }
    } catch (e) {
        // skip: file not found
    }
}

