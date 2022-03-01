import simpleGit, { SimpleGit } from 'simple-git';
import { workbenchOutput } from '../extension';
import { copy, createFolder, remove } from "../helper/toolHelper";
import { Uri, window } from "vscode";
import { getGitInformation } from "../grpc/grpc-handler";
import { GetGitInformationReply_Type } from "../cinco-cloud";

let gitClient: SimpleGit;
export const repositoryPath = '/editor/repository/';

export async function initGitExtension() {
    try {
        await createFolder(repositoryPath);

        gitClient = simpleGit({
            baseDir: repositoryPath,
            binary: 'git',
            maxConcurrentProcesses: 6
        });
    } catch (err) {
        console.log('Could not initialize git client: ' + err);
        workbenchOutput.appendLine('Could not initialize git client: ' + err);
    }
}

export async function pushFilesToRemote(fileUri: Uri) {
    try {
        // get git information
        const gitInformation = await getGitInformation();

        let remoteUrl;

        switch (gitInformation.type) {
            case GetGitInformationReply_Type.NONE:
                throw new Error("Remote repository is not configured.");
            case GetGitInformationReply_Type.BASIC:
                remoteUrl = `https://${gitInformation.username}:${gitInformation.password}@${gitInformation.repositoryUrl}`;
                break;
            case GetGitInformationReply_Type.UNRECOGNIZED:
                throw new Error('Received unknown git information type.');
        }

        const cMsg = await window.showInputBox({
            prompt: 'Commit message: '
        });

        if (cMsg == "") {
            throw new Error('Empty commit message!');
        }

        // clean local repo
        await remove(repositoryPath + '{*,.*}');

        // clone repository
        await gitClient.clone(remoteUrl, repositoryPath);

        // set git author information
        gitClient = gitClient.addConfig('user.name', 'Cinco Cloud')
            .addConfig('user.email', 'cinco.cloud@cinco.cloud');

        // checkout target branch
        if (gitInformation.branch) {
            await gitClient.checkout(gitInformation.branch);
        }

        // check optional targetpath
        let targetPath = repositoryPath;
        if (gitInformation.genSubdirectory) {
            targetPath = targetPath + gitInformation.genSubdirectory;
            await createFolder(targetPath);
        }

        await copy(fileUri.path, targetPath);

        // push to remote
        await gitClient
            .add("./*")
            .commit(cMsg)
            .push();

        await window.showInformationMessage('Successfully pushed to remote repository');

    } catch (err) {
        console.log('Could not push to remote repository: ' + err);
        workbenchOutput.appendLine('Could not push to remote repository: ' + err);
        await window.showErrorMessage('Could not push to remote repository: ' + err);
    }
}