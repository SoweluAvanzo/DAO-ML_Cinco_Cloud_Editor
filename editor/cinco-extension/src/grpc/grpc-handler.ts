import * as fs from "fs";
import { CreateImageRequest, MainServiceClientImpl, GrpcWebImpl } from "../cinco-cloud";

/**
 * TODO: setup host-adresse and fetching of projectId
 */
const host: string = "TODO";
const grpcImpl: GrpcWebImpl = new GrpcWebImpl(host, {});

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