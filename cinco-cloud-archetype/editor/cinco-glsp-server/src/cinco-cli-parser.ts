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

import { CliParser, createSocketCliParser, defaultSocketLaunchOptions, SocketLaunchOptions } from '@eclipse-glsp/server/node';

export interface CincoLaunchOptions extends SocketLaunchOptions {
    rootFolder: string | undefined;
    metaLanguagesFolder: string | undefined;
    workspaceFolder: string | undefined;
    metaDevMode: boolean;
    webServerPort: number | undefined;
    webSocket: boolean;
    transpilationMode: string;
}

export function createCincoCliParser<O extends CincoLaunchOptions = CincoLaunchOptions>(
    defaultOptions: CincoLaunchOptions = {
        rootFolder: undefined,
        metaLanguagesFolder: undefined,
        workspaceFolder: undefined,
        webSocket: false,
        webServerPort: undefined,
        metaDevMode: false,
        transpilationMode: 'NONE',
        ...defaultSocketLaunchOptions
    }
): CliParser<O> {
    const argParser = createSocketCliParser<O>(defaultOptions);
    // add additional args
    argParser.command.option('--rootFolder <rootFolder>', 'Set absolute path of root folder.', undefined);
    argParser.command.option('--metaLanguagesFolder <metaLanguagesFolder>', 'Set path to languages folder, relative to root.', undefined);
    argParser.command.option('--workspaceFolder <workspaceFolder>', 'Set path to workspace folder, relative to root.', undefined);
    argParser.command.option('--metaDevMode', 'Activate dev mode for language designing.', false);
    argParser.command.option('--webServerPort <webServerPort>', 'Port where web content is served', undefined);
    argParser.command.option(
        '--transpilationMode <transpilationMode>',
        'Mode of transpilation of semantic source files [WATCH, ONCE, NONE]',
        undefined
    );
    argParser.command.option('-w , --webSocket', 'Flag to use websocket launcher instead of default launcher', false);
    return argParser;
}
