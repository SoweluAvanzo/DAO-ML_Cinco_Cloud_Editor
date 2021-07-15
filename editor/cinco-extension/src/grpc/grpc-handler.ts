import * as fs from "fs";
import { CreateImageRequest, MainServiceClientImpl, GrpcWebImpl } from "../cinco-cloud";

/**
 * TODO: setup host-adresse and fetching of projectId
 */
const host: string = getCincoCloudHost();
const port: string = getCincoCloudPort();
const grpcImpl: GrpcWebImpl = new GrpcWebImpl(host+":"+port, {});

export function executeProduct(zip: string) {
    var archive = fs.readFileSync(zip);
    const imageRequest: CreateImageRequest = { projectId: getProjectId(), archive: archive };
    const mainService = new MainServiceClientImpl({
        unary: grpcImpl.unary
    });
    mainService.CreateImageFromArchive(imageRequest);
}

function getProjectId() {
    return 0;
}

function getCincoCloudHost(): string {
    const cincocloudHost = process.env.CINCO_CLOUD_HOST;
    return cincocloudHost ? cincocloudHost : 'main-service';
}

function getCincoCloudPort(): string {
    const cincocloudPort = process.env.CINCO_CLOUD_GRPC_PORT;
    return cincocloudPort ? cincocloudPort : '9000';
}