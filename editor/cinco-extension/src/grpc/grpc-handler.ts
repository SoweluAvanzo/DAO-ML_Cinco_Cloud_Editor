import { grpc } from '@improbable-eng/grpc-web';
import * as fs from 'fs';
import { commands, window } from 'vscode';

import { CreateImageRequest, GrpcWebImpl, MainServiceClientImpl } from '../cinco-cloud';
import { workbenchOutput } from '../extension';

const host: string = getCincoCloudHost();
const port: string = getCincoCloudPort();
const metadata: grpc.Metadata = new grpc.Metadata();
const grpcImpl: GrpcWebImpl = new GrpcWebImpl(host + ":" + port, { metadata: metadata });

export async function executeProduct(zip: string) {
    var archive = fs.readFileSync(zip);
    const projectId: number = + await getProjectId();
    const imageRequest: CreateImageRequest = { projectId: projectId, archive: archive };
    const mainService = new MainServiceClientImpl({
        unary: grpcImpl.unary
    });
    mainService.CreateImageFromArchive(imageRequest);
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