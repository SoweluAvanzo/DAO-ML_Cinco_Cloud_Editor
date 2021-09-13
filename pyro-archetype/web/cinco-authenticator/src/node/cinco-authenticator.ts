/* eslint-disable header/header */
import { ApplicationPackage } from '@theia/application-package';
import { ILogger, logger, MaybePromise } from '@theia/core';
import URI from '@theia/core/lib/common/uri';
import { BackendApplicationContribution } from '@theia/core/lib/node';
import * as express from 'express';
import * as http from 'http';
import * as https from 'https';
import { inject, injectable } from 'inversify';
import * as querystring from 'querystring';
import { isDebugging } from './debugHandler';

const LOG_NAME = '[CINCO-AUTHENTICATOR] ';

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
    const allowed = await authenticateJWT(req, res);
    if (allowed) {
        next();
    } else {
        console.log(LOG_NAME + ': blocked user!');
    }
}

async function authenticateJWT(req: any, res: any): Promise<boolean> {
    const query = req.query;
    let jwt = query.jwt;
    let projectId = query.projectId;

    const host = req.host;
    if (isWebviewHost(host)) {
        return true;
    }

    // fallback to referer
    if (!query || !jwt || !projectId) {
        const headers = req.headers;
        const referer = new URI(headers.referer);
        if (isWebviewReferer(referer)) {
            return true;
        }
        const fallbackQuery = referer?.query;
        const fallbackToken = querystring.parse(fallbackQuery);
        jwt = fallbackToken?.jwt;
        projectId = fallbackToken?.projectId;
    }
    const valid = await validateJWT(jwt, projectId, res);
    if (!jwt || !projectId || !valid) {
        block(res);
        return false;
    }
    return true;
}

async function validateJWT(jwt: any, projectId: any, res: any): Promise<boolean> {
    if (isDebugging()) {
        logger.info(LOG_NAME + ': debugging mode on - allow access without authentification');
        return true;
    }
    return new Promise<boolean>((resolve, reject) => {
        // connect to master app and check jwt-token:
        // http://cinco-cloud/api/user/current/private
        const options = {
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
            return resolve(false);
        });
    });
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

function isWebviewReferer(referer: URI): boolean {
    return referer
        && referer.parent
        && referer.parent.path
        && referer.parent.path.name === 'webview';
}

function isWebviewHost(host: string): boolean {
    const re = /(\w+-?)*(\w+)(.)(webview).(\w+)(?::\d+)?/g;
    const matched = host.match(re);
    // eslint-disable-next-line no-null/no-null
    return matched !== null && matched.length > 0;
}
