/* eslint-disable header/header */
import { FrontendApplicationContribution } from '@theia/core/lib/browser';
import { Command, CommandRegistry, MaybePromise } from '@theia/core/lib/common';
import { inject, injectable } from 'inversify';
import * as querystring from 'querystring';

@injectable()
export class CommandProvider implements FrontendApplicationContribution {

    @inject(CommandRegistry)
    protected readonly commandRegistry: CommandRegistry;

    initialize?(): MaybePromise<void> {
        this.registerCommands(this.commandRegistry);
    }

    registerCommands(registry: CommandRegistry): void {
        const PROJECT_ID_PROVIDER: Command = {
            id: 'info.scce.cinco.cloud.projectid'
        };
        registry.registerCommand(PROJECT_ID_PROVIDER, {
            execute: this.passProjectId
        });
        console.log('registered command: ' + PROJECT_ID_PROVIDER.id);

        const JWT_PROVIDER: Command = {
            id: 'info.scce.cinco.cloud.jwt'
        };
        registry.registerCommand(JWT_PROVIDER, {
            execute: this.passJWT
        });
        console.log('registered command: ' + JWT_PROVIDER.id);
    }

    passProjectId(): string {
        const protocol: string = window.location.protocol;
        const host: string = window.location.host;
        const pathname: string = window.location.pathname;
        const search: string = window.location.search;

        const adresse = protocol + '//' + host + pathname + search;
        const urlQuery = querystring.parse(adresse);
        const projectId: string = urlQuery.projectId as string;
        if (!projectId) {
            throw new Error('projectId is undefined! Please provide it.');
        }
        return projectId;
    }

    passJWT(): string {
        const urlSearchParams = new URLSearchParams(window.location.search);
        const params = Object.fromEntries(urlSearchParams.entries());
        const jwt = params.jwt;
        if (!jwt) {
            throw new Error('jwt is undefined! Please provide it.');
        }
        return jwt;
    }
}

