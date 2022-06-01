
import * as fs from 'fs';
import { commands, window } from 'vscode';

import {CreateImageRequest, GetGitInformationReply, MainServiceClient} from '../cinco-cloud';
import { workbenchOutput } from '../extension';
import { ChannelCredentials } from '@grpc/grpc-js';
import { Client } from 'minio';

const host: string = getCincoCloudHost();
const port: string = getCincoCloudPort();

export async function executeProduct(zip: string) {
    try {
        const projectId: number = + await getProjectId();

        const minioClient = new Client({
            endPoint: process.env.MINIO_HOST,
            port: Number(process.env.MINIO_PORT),
            accessKey: process.env.MINIO_ACCESS_KEY,
            secretKey: process.env.MINIO_SECRET_KEY,
            useSSL: false
        });

        const objectName = `project-${projectId}-pyro-server-sources.zip`;
        await minioClient.fPutObject('projects', objectName, zip);
        console.log(`Uploaded archive to Minio: ${objectName}`);

        const imageRequest: CreateImageRequest = { projectId };

        callGrpcImageRequest(imageRequest);
    } catch (e) {
        const message = "Failed to build image!";
        window.showErrorMessage(message);
        console.log("message", e);
    }
}

export function callGrpcImageRequest(imageRequest: CreateImageRequest) {
    const mainService = new MainServiceClient(host + ":" + port, ChannelCredentials.createInsecure());

    mainService.createImageFromArchive(imageRequest, (err, res) => {
        if (err) {
            console.log('Failed to send request to main service.', err);
            throw err;
        } else {
            console.log('Send request to main service.', res);
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

export async function getGitInformation(): Promise<GetGitInformationReply> {
    const projectId: number = + await getProjectId();
    const mainService = new MainServiceClient(host + ":" + port, ChannelCredentials.createInsecure());

    return new Promise((resolve, reject) => {
        mainService.getGitInformation({ projectId }, (err, res) => {
            if (err) {
                console.log('Failed to retrieve git information from main service.', err);
                throw new Error();
            } else {
                console.log('Retrieved git information from main service.');
                resolve(res);
            }
        });
    });
}

function getCincoCloudHost(): string {
    const cincocloudHost = process.env.CINCO_CLOUD_HOST;
    return cincocloudHost ? cincocloudHost : 'main-service';
}

function getCincoCloudPort(): string {
    const cincocloudPort = process.env.CINCO_CLOUD_GRPC_PORT;
    return cincocloudPort ? cincocloudPort : '9000';
}
