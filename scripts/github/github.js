const {request} = require("@octokit/request");
const yargs = require('yargs');

const fetchRepos = async (token, perPage, page) => {
    let result = [];

    let res = await doFetchRepos(token, perPage, page);

    if (res.status === 200) {
        result.push.apply(result, res.data.items);

        let numberOfPage = Math.ceil(res.data.total_count / perPage);

        if (page <= numberOfPage) {
            page = page + 1;
            result.push.apply(result, await fetchRepos(token, perPage, page));
        }
    }

    return result;
};

async function doFetchRepos(token, perPage, page) {
    return await request({
        headers: {
            Authorization: `token ${token}`
        },
        method: 'GET',
        url: `/search/repositories?q=${encodeURIComponent('org:enonic')}&per_page=${perPage}&page=${page}`
    }).then((res) => {
        return res;
    }).catch(err => {
        console.error(`Error: ${err}`);
    });
}

const argv = yargs.command('find <token> <repoNameRegExp> [fileLocation] [contentRegExp]',
    'Returns repository names of the specific organization by the criteria',
    (yargs) => {
        yargs.positional('token', {
            describe: 'The personal access token. In order to set it up to visit https://github.com/settings/tokens page.',
            type: 'string'
        }).positional('repoNameRegExp', {
            describe: 'RegExp pattern for a repository name.',
            type: 'string'
        }).positional('fileLocation', {
            describe: 'File location.',
            type: 'string'
        }).positional('contentRegExp', {
            describe: 'RegExp pattern for a content.',
            type: 'string'
        })
    }, async (argv) => {
        let isContainsFile = true;
        if (argv['not-contains-file']) {
            isContainsFile = false;
        }

        let repositories = await fetchRepos(argv.token, 100, 1);

        let repositoryNames = repositories.filter(repository => doFilterRepo(repository, argv)).map(repository => {
            return repository.name;
        });

        let resultForContains = [];
        let resultForNotContains = [];

        for (const repositoryName of repositoryNames) {

            if (argv.fileLocation && argv.fileLocation.length > 0) {
                let url = `/repos/enonic/${repositoryName}/contents/${argv.fileLocation}`;
                await request({
                    method: 'GET',
                    headers: {
                        Authorization: `token ${argv.token}`,
                        Accept: 'application/vnd.github.v3.raw'
                    },
                    url: url
                }).then(res => {
                    if (argv.contentRegExp && argv.contentRegExp.length > 0) {
                        let data = res.data.split('\n');

                        data.some(function (input) {
                            const match = input.match(new RegExp(argv.contentRegExp));
                            if (match) {
                                resultForContains.push(repositoryName);
                                console.log(`${repositoryName} ${match.groups.out || input }`)
                                return true;
                            }
                        });
                    } else {
                        resultForContains.push(repositoryName);
                    }
                }).catch(err => {
                    if (err.status === 404) {
                        resultForNotContains.push(repositoryName);
                    } else {
                        console.error(err);
                    }
                    return null
                });
            } else {
                resultForContains.push(repositoryName);
            }
        }

        console.log(isContainsFile === true ? resultForContains.join(' ') : resultForNotContains.join(' '));
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
    .option('not-contains-file', {
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
        repoNameMatched = new RegExp(argv.repoNameRegExp).test(repository.name);
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
