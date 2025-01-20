/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/

import { Action } from './shared-protocol';

export const ARGS_PROVIDER_ID = 'cinco.provide.glsp-server-args';

export interface ServerArgs {
    metaDevMode: boolean;
    rootFolder: string;
    languagePath: string;
    workspacePath: string;
    port: number;
    websocketPath: string;
    webServerPort: number;
    useSSL: boolean;
    startUpTranspilation: TranspilationMode;
    webServerHostMapping?: string;
    websocketHostMapping?: string;
}

export enum TranspilationMode {
    NONE,
    ONCE,
    WATCH
}

export namespace TranspilationMode {
    export function fromString(value: string | undefined): TranspilationMode | undefined {
        if (value === 'NONE') {
            return TranspilationMode.NONE;
        } else if (value === 'ONCE') {
            return TranspilationMode.ONCE;
        } else if (value === 'WATCH') {
            return TranspilationMode.WATCH;
        }
        return undefined;
    }

    export function toString(value: TranspilationMode | undefined): string | undefined {
        if (value === TranspilationMode.NONE) {
            return 'NONE';
        } else if (value === TranspilationMode.ONCE) {
            return 'ONCE';
        } else if (value === TranspilationMode.WATCH) {
            return 'WATCH';
        }
        return undefined;
    }
}

export namespace ServerArgs {
    export function create(
        metaDevMode: boolean,
        rootFolder: string,
        languagePath: string,
        workspacePath: string,
        port: number,
        websocketPath: string,
        webServerPort: number,
        useSSL: boolean,
        startUpTranspilation: TranspilationMode,
        webServerHostMapping?: string,
        websocketHostMapping?: string
    ): ServerArgs {
        return {
            metaDevMode: metaDevMode,
            rootFolder: rootFolder,
            languagePath: languagePath,
            workspacePath: workspacePath,
            port: port,
            websocketPath: websocketPath,
            webServerPort: webServerPort,
            useSSL: useSSL,
            webServerHostMapping: webServerHostMapping,
            websocketHostMapping: websocketHostMapping,
            startUpTranspilation: startUpTranspilation ?? TranspilationMode.NONE
        };
    }
}

export interface ServerArgsRequest extends Action {
    kind: typeof ServerArgsRequest.KIND;
}

export namespace ServerArgsRequest {
    export const KIND = 'cinco.glsp-server-args.request';

    export function create(): ServerArgsRequest {
        return {
            kind: KIND
        } as ServerArgsRequest;
    }
}

export interface ServerArgsResponse extends Action {
    kind: typeof ServerArgsResponse.KIND;
    serverArgs: ServerArgs;
}

export namespace ServerArgsResponse {
    export const KIND = 'cinco.glsp-server-args.response';

    export function create(serverArgs: ServerArgs): ServerArgsResponse {
        return {
            kind: KIND,
            serverArgs: serverArgs
        } as ServerArgsResponse;
    }
}
