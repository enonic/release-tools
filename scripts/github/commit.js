const yargs = require('yargs');
const githubUtils = require('./utils');
const fs = require('fs');

const argv = yargs.command('commit <token> <dir> <comment> <repos>',
    'GitHub commit. Allows to commit multiple files in one commit.',
    (yargs) => {
        yargs.positional('token', {
            describe: 'The personal access token. In order to set it up to visit https://github.com/settings/tokens page.',
            type: 'string'
        }).positional('dir', {
            describe: 'Path to directory with files for commit.',
            type: 'string'
        }).positional('comment', {
            describe: 'Comment for GitHub commit',
            type: 'string'
        }).positional('repo', {
            describe: 'Repository names space separated.',
            type: 'string'
        })
    }, async (argv) => {
        await updateGradleWrapper(argv, 'enonic', 'master');
    })
    .help()
    .alias('help', 'h')
    .argv;


function doBuildDistributionsConfig(baseDir, dir) {
    let result = [];
    fs.readdirSync(dir).forEach(function (element) {
        let path = dir + "/" + element;
        let stat = fs.statSync(path);
        if (stat.isDirectory()) {
            result = result.concat(doBuildDistributionsConfig(baseDir, path));
        } else {
            let mode = (stat.mode & parseInt('777', 8)).toString(8);
            result.push({
                content: fs.readFileSync(path),
                path: path.replace(baseDir + '/', ''),
                mode: '100' + (mode.startsWith('7') ? '755' : '644')
            });
        }
    });
    return result;
}

function buildDistributionsConfig(dir) {
    return doBuildDistributionsConfig(dir, dir);
}

async function updateGradleWrapper(argv, owner, branch) {
    let distributionsConfig = buildDistributionsConfig(argv.dir);

    let repositories = argv.repos.split(' ') || [];
    repositories.forEach(async function (repository) {
        try {
            await updateGradleWrapperByRepository(argv.token, owner, repository, branch, argv.comment, distributionsConfig);
        } catch (e) {
            console.log(`Command for the "${repository}" repository is failed ` + e);
        }
    });
}

async function updateGradleWrapperByRepository(token, owner, repository, branch, comment, distributionsConfig) {
    let branchInfo = await githubUtils.getBranchInfo(token, owner, repository, branch);

    let latestCommit = branchInfo.data.commit.sha;

    let treeItems = [];

    for (let index = 0; index < distributionsConfig.length; index++) {
        let distributionConfig = distributionsConfig[index];
        try {
            let blobResponse = await githubUtils.creteBlob(token, owner, repository, distributionConfig.content);
            if (blobResponse.status === 201) {
                treeItems.push({
                    path: distributionConfig.path,
                    sha: blobResponse.data.sha,
                    mode: distributionConfig.mode,
                    type: 'blob'
                });
            }
        } catch (e) {
            console.log(`Error: ${e}`);
        }
    }

    await githubUtils.commit(token, owner, repository, branch, comment, treeItems, latestCommit);
}
