import { RequestType } from "vscode-languageclient";

/**
 * active ast request
 */
export namespace GenerateRequestEndpoint {
    export const type: RequestType<GenerateRequest, any, GenerateResponse, any> = new RequestType('cinco/generate');
}

/******************************************************************************* */

export class GenerateRequest {
    sourceUri: string;
    targetUri: string;
    execute: boolean;
}

export class GenerateResponse {
    targetUri: string;
}