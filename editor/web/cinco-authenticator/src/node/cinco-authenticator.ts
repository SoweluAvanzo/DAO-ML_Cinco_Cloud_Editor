/* eslint-disable header/header */
import { ILogger, logger, MaybePromise } from '@theia/core';
import { BackendApplicationContribution } from '@theia/core/lib/node';
import { ApplicationPackage } from '@theia/application-package';
import { inject, injectable } from 'inversify';
import * as https from 'https';
import * as http from 'http';
import * as express from 'express';
import * as querystring from 'querystring';
import URI from '@theia/core/lib/common/uri';

const LOG_NAME = '[CINCO] ';

@injectable()
export class CincoAuthenticator implements BackendApplicationContribution {
    @inject(ILogger) private readonly logger: ILogger;

    @inject(ApplicationPackage)
    protected readonly applicationPackage: ApplicationPackage;

    onStart?(_server: http.Server | https.Server): MaybePromise<void> {
        this.logInfo('starting CINCO-Authenticator!');
    }

    initialize(): void {
        this.logInfo('initializing CINCO-Authenticator');
    }

    configure?(app: express.Application): MaybePromise<void> {
        app.use(/^(\/.+|(?!\/).*)$/, authenticationFilter);
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

async function authenticationFilter(req: any, res: any, next: any): Promise<void> {
    if (await authenticateJWT(req, res)) {
        next();
    }
}

async function authenticateJWT(req: any, res: any): Promise<boolean> {
    const query = req.query;
    let jwt = query.jwt;
    let projectId = query.projectId;

    // fallback to referer
    if (!query || !jwt || !projectId) {
        const headers = req.headers;
        const referer = new URI(headers.referer);
        const fallbackQuery = referer?.query;
        const fallbackToken = querystring.parse(fallbackQuery);
        jwt = fallbackToken?.jwt;
        projectId = fallbackToken?.projectId;
    }
    if (!jwt || !projectId || !await validateJWT(jwt, projectId, res)) {
        block(res);
        return false;
    }
    return true;
}

async function validateJWT(jwt: any, projectId: any, res: any): Promise<boolean> {
    // logger.info(LOG_NAME + 'jwt = ' + jwt);
    return new Promise<boolean>((resolve, reject) => {

        // connect to master app and check jwt-token:
        // http://cinco-cloud/api/user/current/private
        const options = { // TODO: parameterize
            hostname: getCincoCloudHost(),
            port: getCincoCloudPort(),
            path: getCincoCloudPath(projectId),
            method: 'GET',
            'headers': {
                'Authorization': 'Bearer ' + jwt,
                'Content-Type': 'application/json'
            }
        };
        const request = http.get(options, (response: http.IncomingMessage) => {
            if (response.statusCode === 200) {
                return resolve(true);
            }
            return resolve(false);
        });
        request.addListener('error', (e: any) => {
            // if request lead to an error
            logger.error(LOG_NAME + `: ${e}`);
            if (isDebugging()) {
                return resolve(true);
            }
            return resolve(false);
        });
    });
}

/**
 * If this is executed as a child of the vscode terminal process,
 * it ignores authentification-failures, for easier development.
 */
function isDebugging(): boolean {
    const debugging = process.env.CINCO_CLOUD_DEBUG;
    if (debugging === 'true') {
        logger.info(LOG_NAME + ': debugging mode on - allow access without authentification');
        return true;
    }
    return false;
}

function block(res: any): void {
    res.send(403);
}

function getCincoCloudHost(): string {
    const cincocloudHost = process.env.CINCO_CLOUD_HOST;
    return cincocloudHost ? cincocloudHost : 'main-service';
}

function getCincoCloudPort(): string {
    const cincocloudPort = process.env.CINCO_CLOUD_PORT;
    return cincocloudPort ? cincocloudPort : '8000';
}

function getCincoCloudPath(projectId: any): string {
    return '/api/project/' + projectId;
}
