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

async function doFetchRepos(owner, token, perPage, page) {
    let organization = encodeURIComponent(`org:${owner}`);
    return await request({
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

async function readFile(token, owner, repository, fileLocation) {
    let url = `/repos/${owner}/${repository}/contents/${fileLocation}`;

    return await request({
        method: 'GET',
        headers: {
            Authorization: `token ${token}`
        },
        url: url
    }).then(res => {
        return res;
    }).catch(err => {
        if (err.status === 404) {
            console.warn(`${url} not found`);
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

exports.fetchRepos = fetchRepos;
exports.readFile = readFile;
exports.updateContent = updateContent;
