const {request} = require("@octokit/request");
const yargs = require('yargs');

const fetchRepos = async (token, query, perPage, page) => {
    let result = [];

    let res = await doFetchRepos(token, query, perPage, page);

    if (res.status === 200) {
        result.push.apply(result, res.data.items);

        let numberOfPage = Math.ceil(res.data.total_count / perPage);

        if (page <= numberOfPage) {
            page = page + 1;
            result.push.apply(result, await fetchRepos(token, query, perPage, page));
        }
    }

    return result;
};

async function doFetchRepos(token, query, perPage, page) {
    return await request({
        headers: {
            Authorization: `token ${token}`
        },
        method: 'GET',
        url: `/search/repositories?q=${encodeURIComponent(query)}&per_page=${perPage}&page=${page}`
    }).then((res) => {
        return res;
    }).catch(err => {
        console.error(`Error: ${err}`);
    });
}

const argv = yargs.command('repos <token> [name] [delimiter]', 'Returns repositories of the specific organization by the criteria',
    (yargs) => {
        yargs
            .positional('token', {
                describe: 'The personal access token. In order to set it up to visit https://github.com/settings/tokens page.',
                type: 'string'
            })
            .positional('name', {
                describe: 'RegExp pattern for a repository name.',
                type: 'string'
            })
            .positional('delimiter', {
                describe: 'The delimiter',
                type: 'string',
                default: "\n"
            })
    }, async (argv) => {
        let query = `org:enonic`;

        if (argv.private) {
            query += ` is:private`;
        }

        if (argv['not-archived']) {
            query += ` archived:false`;
        }
        if (argv.archived) {
            query += ` archived:true`;
        }

        if (argv['not-mirror']) {
            query += ` mirror:false`;
        }
        if (argv.mirror) {
            query += ` mirror:true`;
        }

        if (argv.fork) {
            query += ` fork:only`;
        } else {
            query += ` fork:true`
        }

        let repositories = await fetchRepos(argv.token, query, 100, 1);

        let repositoryNames = repositories.filter(repository => {
            if (argv.name) {
                return RegExp(argv.name).test(repository.name);
            }
            return true;
        }).map(repository => {
            return repository.name;
        });

        console.log(repositoryNames.join(argv.delimiter));
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
