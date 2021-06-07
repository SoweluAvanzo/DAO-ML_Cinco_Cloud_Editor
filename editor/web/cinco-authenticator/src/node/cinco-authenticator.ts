/* eslint-disable header/header */
import { ILogger, MaybePromise } from '@theia/core';
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

function authenticationFilter(req: any, res: any, next: any): void {
    const query = req.query;
    const jwt = query.jwt;

    if (!query || !jwt) {
        const headers = req.headers;
        const referer = new URI(headers.referer);
        const fallbackQuery = referer?.query;
        const fallbackToken = querystring.parse(fallbackQuery);

        if (!referer || !fallbackToken || !fallbackToken.jwt) {
            block(res);
        }
    }
    authenticateJWT(res);
    next();
}

function block(res: any): void {
    // res.send(403);
}

function authenticateJWT(res: any): void {
    // res.send(200);
}
