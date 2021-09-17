
import * as fs from 'fs';
import { commands, window } from 'vscode';

import { CreateImageRequest, MainServiceClient } from '../cinco-cloud';
import { workbenchOutput } from '../extension';
import { ChannelCredentials } from '@grpc/grpc-js';

const host: string = getCincoCloudHost();
const port: string = getCincoCloudPort();

export async function executeProduct(zip: string) {
    try {
        // data
        const archive = fs.readFileSync(zip);
        const projectId: number = + await getProjectId();
        const imageRequest: CreateImageRequest = { projectId: projectId, archive: archive };

        callGrpcImageRequest(imageRequest);
    } catch (e) {
        window.showErrorMessage("Failed to execute product.");
        console.log("Failed to execute product.", e);
    }
}

export function callGrpcImageRequest(imageRequest: CreateImageRequest) {
    const mainService = new MainServiceClient(host + ":" + port, ChannelCredentials.createInsecure());

    mainService.createImageFromArchive(imageRequest, (err, res) => {
        if (err) {
            console.log('Failed to send archive to main service.', err);
            throw err;
        } else {
            console.log('Send archive to main service.', res);
        }
    });
}

async function getProjectId(): Promise<string> {
    try {
        const projectId: string = await commands.executeCommand("info.scce.cinco.cloud.projectid");
        console.log("received projectId: " + projectId);
        return projectId;
    } catch (e) {
        const message = "projectid command let to an error!";
        window.showErrorMessage(message);
        workbenchOutput.appendLine(message);
        throw new Error(e);
    }
}

function getCincoCloudHost(): string {
    const cincocloudHost = process.env.CINCO_CLOUD_HOST;
    return cincocloudHost ? cincocloudHost : 'main-service';
}

function getCincoCloudPort(): string {
    const cincocloudPort = process.env.CINCO_CLOUD_GRPC_PORT;
    return cincocloudPort ? cincocloudPort : '9000';
}