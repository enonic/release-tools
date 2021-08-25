const yargs = require('yargs');
const yaml = require('js-yaml');
const utils = require('./utils');

const argv = yargs.command('run <token> <packageEcosystem> <interval> <repo>',
    'Description ...',
    (yargs) => {
        yargs.positional('token', {
            describe: 'The personal access token. In order to set it up to visit https://github.com/settings/tokens page.',
            type: 'string'
        }).positional('packageEcosystem', {
            describe: 'RegExp pattern for a package-ecosystem.',
            type: 'string'
        }).positional('interval', {
            describe: 'Schedule interval',
            type: 'string'
        }).positional('repo', {
            describe: 'The name of the repository for which the changes are applied. If you want to specify more than one repository use space between repository names.',
            type: 'string'
        })
    }, async (argv) => {
        let owner = 'enonic';
        let fileLocation = '.github/dependabot.yml';

        let repositories = argv.repo.split(' ');

        if (repositories.length === 0) {
            console.error("Error: The \"repo\" parameter can not be empty.");
            return;
        }

        for (const repositoryName of repositories) {
            await readAndUpdateFileIfNeeded(owner, repositoryName, fileLocation, argv);
        }
    })
    .help()
    .alias('help', 'h')
    .argv;

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

                        const updatedDocument = yaml.dump(configAsJson, {
                            forceQuotes: true,
                            quotingType: '"'
                        });
                        await utils.updateContent(argv.token, owner, repository, fileLocation, response.data.sha, updatedDocument);
                    }
                    break;
                }
            }
        }
    }
}
