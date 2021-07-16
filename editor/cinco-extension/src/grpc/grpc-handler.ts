
import * as fs from 'fs';
import { commands, window } from 'vscode';
import {grpc} from "@improbable-eng/grpc-web";
import { NodeHttpTransport } from "@improbable-eng/grpc-web-node-http-transport";


import { CreateImageRequest, GrpcWebImpl, MainServiceClientImpl } from '../cinco-cloud';
import { workbenchOutput } from '../extension';

const host: string = getCincoCloudHost();
const port: string = getCincoCloudPort();

export async function executeProduct(zip: string) {
    // data
    var archive = fs.readFileSync(zip);
    const projectId: number = + await getProjectId();
    const imageRequest: CreateImageRequest = { projectId: projectId, archive: archive };

    callGrpcImageRequest(imageRequest);
}

function getMetadata() {
    const metadata = new grpc.Metadata();
    metadata.set("grpc-status", grpc.Code.OK.toString());
    metadata.set("grpc-message", "OK");
    return metadata;
}

export function callGrpcImageRequest(imageRequest: CreateImageRequest) {
    const metadata: grpc.Metadata = getMetadata();
    const grpcImpl: GrpcWebImpl = new GrpcWebImpl(host + ":" + port,
        {
            metadata: metadata,
            transport: NodeHttpTransport()
        });

    const mainService = new MainServiceClientImpl({
        unary: (methodDesc, _request, metadata) => grpcImpl.unary(methodDesc, _request, metadata)
    });
    mainService.CreateImageFromArchive(imageRequest, metadata);
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