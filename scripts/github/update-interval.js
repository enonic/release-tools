const yargs = require('yargs');
const yaml = require('js-yaml');
const utils = require('./utils');

const argv = yargs.command('update-schedule-interval <token> <repoNameRegExp> <packageEcosystem> <interval>',
    'Description ...',
    (yargs) => {
        yargs.positional('token', {
            describe: 'The personal access token. In order to set it up to visit https://github.com/settings/tokens page.',
            type: 'string'
        }).positional('repoNameRegExp', {
            describe: 'RegExp pattern for a repository name.',
            type: 'string'
        }).positional('packageEcosystem', {
            describe: 'RegExp pattern for a package-ecosystem.',
            type: 'string'
        }).positional('interval', {
            describe: 'Schedule interval',
            type: 'string'
        })
    }, async (argv) => {
        let owner = 'enonic';
        let fileLocation = '.github/dependabot.yml';

        let repositories = await utils.fetchRepos(owner, argv.token, 100, 1);

        let repositoryNames = repositories.filter(repository => doFilterRepo(repository, argv)).map(repository => {
            return repository.name;
        });

        for (const repositoryName of repositoryNames) {
            await readAndUpdateFileIfNeeded(owner, repositoryName, fileLocation, argv);
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

async function readAndUpdateFileIfNeeded(owner, repository, fileLocation, argv) {
    let response = await utils.readFile(argv.token, owner, repository, fileLocation);

    if (response.status === 200) {
        let content = Buffer.from(response.data.content, 'base64').toString('utf8');

        const configAsJson = yaml.load(content, 'utf8');

        if (configAsJson && configAsJson.updates) {
            for (let index = 0; index < configAsJson.updates.length; index++) {
                let updateConfig = configAsJson.updates[index];
                if (updateConfig['package-ecosystem'] === argv.packageEcosystem) {
                    if (updateConfig.schedule.interval !== argv.interval) {
                        updateConfig.schedule.interval = argv.interval;

                        const updatedDocument = yaml.dump(configAsJson);
                        await utils.updateContent(argv.token, owner, repository, fileLocation, response.data.sha, updatedDocument);
                    }
                    break;
                }
            }
        }
    }
}
