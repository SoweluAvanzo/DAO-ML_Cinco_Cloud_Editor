/* eslint-disable header/header */
import { ILogger, MaybePromise } from '@theia/core';
import { BackendApplicationContribution } from '@theia/core/lib/node';
import { ApplicationPackage } from '@theia/application-package';
import { inject, injectable } from 'inversify';
import * as https from 'https';
import * as http from 'http';
import * as CincoCloudGrpcDefinitions from './cinco-cloud';

const LOG_NAME = '[CINCO] ';

@injectable()
export class CincoCloudGrpc implements BackendApplicationContribution {
    @inject(ILogger) private readonly logger: ILogger;

    @inject(ApplicationPackage)
    protected readonly applicationPackage: ApplicationPackage;

    readonly Definitions = CincoCloudGrpcDefinitions;

    onStart?(_server: http.Server | https.Server): MaybePromise<void> {
        this.logInfo('starting CINCO-Cloud-GRPC!');
    }

    initialize(): void {
        this.logInfo('initializing CINCO-Authenticator');
    }

    validate(id: string, callback: (err: string | undefined, response: string) => void): void {
        callback(undefined, 'response');
    }

    protected logError(data: string | Buffer): void {
        if (data) {
            this.logger.error(LOG_NAME + `: ${data}`);
        }
    }

    protected logInfo(data: string | Buffer): void {
        if (data) {
            this.logger.info(LOG_NAME + `: ${data}`);
        }
    }
}
