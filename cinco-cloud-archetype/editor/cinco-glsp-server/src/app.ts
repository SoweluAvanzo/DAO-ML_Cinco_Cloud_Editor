/********************************************************************************
 * Copyright (c) 2022 Cinco Cloud.
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

import { createAppModule, createSocketCliParser, ServerModule, SocketServerLauncher } from '@eclipse-glsp/server/node';
import { Container } from 'inversify';
import { CincoDiagramModule } from './diagram/cinco-diagram-module';

export async function launch(argv?: string[]): Promise<void> {
    const argParser = createSocketCliParser();

    // add additional args
    argParser.command.option('--rootFolder <rootFolder>', 'Set absolute path of root folder.', undefined);
    argParser.command.option('--metaLanguagesFolder <metaLanguagesFolder>', 'Set path to languages folder, relative to root.', undefined);
    argParser.command.option('--workspaceFolder <workspaceFolder>', 'Set path to workspace folder, relative to root.', undefined);
    argParser.command.option('--metaDevMode', 'Activate dev mode for language designing.');

    const options = argParser.parse(argv);
    const appContainer = new Container();
    appContainer.load(createAppModule(options));
    const launcher = appContainer.resolve(SocketServerLauncher);
    const serverModule = new ServerModule().configureDiagramModule(new CincoDiagramModule());

    launcher.configure(serverModule);
    launcher.start({ port: options.port, host: options.host });
}
