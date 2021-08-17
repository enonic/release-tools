const {request} = require("@octokit/request");

async function fetchRepos(owner, token, perPage, page) {
    let result = [];

    let res = await doFetchRepos(owner, token, perPage, page);

    if (res.status === 200) {
        result.push.apply(result, res.data.items);

        let numberOfPage = Math.ceil(res.data.total_count / perPage);

        if (page <= numberOfPage) {
            page = page + 1;
            result.push.apply(result, await fetchRepos(owner, token, perPage, page));
        }
    }

    return result;
}

function doFetchRepos(owner, token, perPage, page) {
    let organization = encodeURIComponent(`org:${owner}`);
    return request({
        headers: {
            Authorization: `token ${token}`
        },
        method: 'GET',
        url: `/search/repositories?q=${organization}&per_page=${perPage}&page=${page}`
    }).then((res) => {
        return res;
    }).catch(err => {
        console.error(`Error: ${err}`);
    });
}

function readFile(token, owner, repository, fileLocation, showErrors) {
    let url = `/repos/${owner}/${repository}/contents/${fileLocation}`;

    return request({
        method: 'GET',
        headers: {
            Authorization: `token ${token}`
        },
        url: url
    }).then(res => {
        return res;
    }).catch(err => {
        if (err.status === 404) {
            if (showErrors === true) {
                console.warn(`${url} not found`);
            }
            return null;
        } else {
            throw err;
        }
    });
}

async function updateContent(token, owner, repository, fileLocation, sha, content) {
    let url = `/repos/${owner}/${repository}/contents/${fileLocation}`;

    let data = {
        content: Buffer.from(content).toString('base64'),
        message: `Updated ${fileLocation} file`
    };

    if (sha !== null) {
        data.sha = sha;
    }

    await request({
        headers: {
            Authorization: `token ${token}`
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

function getBranchInfo(token, owner, repository, branch) {
    return request({
        headers: {
            Authorization: `token ${token}`
        },
        method: 'GET',
        url: `/repos/${owner}/${repository}/branches/${branch}`
    }).then((res) => {
        return res;
    }).catch(err => {
        console.error(`Error: ${err}`);
    });
}

function creteBlob(token, owner, repository, content) {
    return request({
        headers: {
            Authorization: `token ${token}`
        },
        method: 'POST',
        url: `/repos/${owner}/${repository}/git/blobs`,
        data: {
            content: content,
            encoding: 'utf-8'
        }
    });
}

function createTree(token, owner, repository, treeItems, baseTreeOrLatestCommitSHA) {
    return request({
        headers: {
            Authorization: `token ${token}`
        },
        method: 'POST',
        url: `/repos/${owner}/${repository}/git/trees`,
        data: {
            tree: treeItems,
            base_tree: baseTreeOrLatestCommitSHA
        }
    });
}

function createCommit(token, owner, repository, msg, treeSHA, baseTreeOrLatestCommitSHA) {
    return request({
        headers: {
            Authorization: `token ${token}`
        },
        method: 'POST',
        url: `/repos/${owner}/${repository}/git/commits`,
        data: {
            message: msg,
            tree: treeSHA,
            parents: [
                baseTreeOrLatestCommitSHA
            ]
        }
    });
}

function updateRef(token, owner, repository, ref, commitSHA) {
    return request({
        headers: {
            Authorization: `token ${token}`
        },
        method: 'PATCH',
        url: `/repos/${owner}/${repository}/git/refs/${ref}`,
        data: {
            sha: commitSHA
        }
    })
}

async function commit(token, owner, repository, branch, comment, treeItems, latestCommit) {
    try {
        let createTreeResponse = await createTree(token, owner, repository, treeItems, latestCommit);

        if (createTreeResponse.status === 201) {
            let createCommitResponse = await createCommit(token, owner, repository,
                comment,
                createTreeResponse.data.sha,
                latestCommit);

            if (createCommitResponse.status === 201) {
                let updateRefResponse = await updateRef(token, owner, repository,
                    `heads/${branch}`,
                    createCommitResponse.data.sha);

                if (updateRefResponse.status === 200) {
                    console.log('Command successfully completed');
                }
            }
        }
    } catch (e) {
        console.error(e);
    }
}

exports.fetchRepos = fetchRepos;
exports.readFile = readFile;
exports.updateContent = updateContent;
exports.getBranchInfo = getBranchInfo;
exports.creteBlob = creteBlob;
exports.createTree = createTree;
exports.createCommit = createCommit;
exports.updateRef = updateRef;
exports.commit = commit;
