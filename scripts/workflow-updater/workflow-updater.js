const {request} = require("@octokit/request");

const AUTH_INDEX = 0;


async function getFileInfo(owner, repo, path) {
    return await request(`GET /repos/${owner}/${repo}/contents/${path}`);
}

async function getShaIfFileExists(owner, repo, path) {
    try {
        const res = await getFileInfo(owner, repo, path);

        return res.data.sha;
    } catch (e) {
        return null;
    }
}

async function updateWorkflow(owner, repo, path, content) {
    let data = {
        message: 'Update workflow',
        content: content
    };

    let fileSHA = await getShaIfFileExists(owner, repo, path);

    if (fileSHA != null) {
        data.sha = fileSHA;
    }

    return await request({
        headers: {
            Authorization: `token ${process.argv.slice(2)[AUTH_INDEX]}`
        },
        method: 'PUT',
        url: `/repos/${owner}/${repo}/contents/${path}`,
        data: data
    });
}

function execute() {
    getFileInfo('enonic', '.github', 'workflow-templates/enonic-gradle.yml')
        .then(template => {
            let repos = process.argv.slice(3);

            repos.forEach(async (repo) => {
                await updateWorkflow('enonic', repo, '.github/workflows/enonic-gradle.yml', template.data.content)
                    .then(res => {
                        console.log(`Repo "${repo}" was updated`);
                    })
                    .catch(err => {
                        console.log(JSON.stringify(err, null, 4));
                        console.log(`Repo "${repo}" was not updated`);
                    });
            });

        }).catch(err => console.log(JSON.stringify(err, null, 4)));
}

execute();
