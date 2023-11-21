/********************************************************************************
 * Copyright (c) 2019-2023 EclipseSource and others.
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
import { BaseGLSPClientContribution, GLSPDiagramManager, WebSocketConnectionOptions } from '@eclipse-glsp/theia-integration/lib/browser';
import { EnvVariablesServer } from '@theia/core/lib/common/env-variables';
import { inject, injectable, multiInject } from '@theia/core/shared/inversify';
import { getDiagramConfiguration } from '../common/cinco-language';
import {
    DIAGRAM_TYPE,
    MetaSpecificationReloadCommand,
    DEFAULT_WEBSOCKET_PORT_KEY,
    DEFAULT_WEBSOCKET_PORT
} from '@cinco-glsp/cinco-glsp-common';

import { CommandRegistry, SelectionService } from '@theia/core';
import { ActionMessage, InitializeClientSessionParameters } from '@eclipse-glsp/protocol';
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { LabelProvider, OpenerService } from '@theia/core/lib/browser';
import { MetaSpecificationReloadCommandHandler } from './meta/meta-specification-reload-command-handler';
import { WorkspaceService } from '@theia/workspace/lib/browser';

@injectable()
export class CincoGLSPClientContribution extends BaseGLSPClientContribution {
    @inject(EnvVariablesServer)
    protected readonly envVariablesServer: EnvVariablesServer; // this could be used for env vars for connection
    @inject(CommandRegistry)
    protected readonly commandRegistry: CommandRegistry;

    @inject(LabelProvider) protected readonly labelProvider: LabelProvider;
    @inject(FileService) protected readonly fileService: FileService;
    @inject(OpenerService) protected readonly openerService: OpenerService;
    @inject(SelectionService) protected readonly selectionService: SelectionService;
    @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService;
    @multiInject(GLSPDiagramManager) protected diagramManagers: GLSPDiagramManager[];

    readonly id = getDiagramConfiguration().contributionId;
    readonly fileExtensions = getDiagramConfiguration().fileExtensions;

    static readonly SYSTEM_ID = 'SYSTEM';

    constructor() {
        super();
        this.initializeSystemSession(CincoGLSPClientContribution.SYSTEM_ID);
    }

    protected override async getWebSocketConnectionOptions(): Promise<WebSocketConnectionOptions | undefined> {
        const webSocketPort = await this.getWebSocketPortFromEnv();
        if (webSocketPort) {
            return {
                path: this.id,
                port: webSocketPort
            };
        }
        return undefined;
    }

    protected async getWebSocketPortFromEnv(): Promise<number | undefined> {
        const envVar = await this.envVariablesServer.getValue(DEFAULT_WEBSOCKET_PORT_KEY);
        if (envVar && envVar.value) {
            const webSocketPort = Number.parseInt(envVar.value, 10);
            if (isNaN(webSocketPort) || webSocketPort < 0 || webSocketPort > 65535) {
                throw new Error('Value of environment variable ' + DEFAULT_WEBSOCKET_PORT_KEY + ' is not a valid port');
            }
            return webSocketPort;
        }
        return DEFAULT_WEBSOCKET_PORT;
    }

    initializeSystemSession(id: string): void {
        console.log('preparing system-glsp-client (1/4)...');
        this.glspClient.then(client => {
            console.log('system-glsp-client ready! (2/4)');
            this.initialize(client).then(_v => {
                console.log('system-glsp-client connecting... (3/4)');
                client
                    .initializeClientSession({
                        clientSessionId: id,
                        diagramType: DIAGRAM_TYPE
                    } as InitializeClientSessionParameters)
                    .then(_v2 => {
                        console.log('system-glsp-client connected! (4/4)');
                        console.log('registering: ' + MetaSpecificationReloadCommand.ID);
                        this.commandRegistry.registerCommand(
                            { id: MetaSpecificationReloadCommand.ID, label: 'Reload Meta-Specification', category: 'Cinco Cloud' },
                            new MetaSpecificationReloadCommandHandler(
                                client,
                                this.commandRegistry,
                                this.labelProvider,
                                this.workspaceService,
                                this.selectionService,
                                this.fileService,
                                this.openerService,
                                // this callback will overwrite the glsp-clients onActionMessage...
                                (m: ActionMessage) =>
                                    new Promise<void>(resolve => {
                                        /*
                                        let counter = 0;
                                        this.diagramManagers.forEach(diagramManager => {
                                            // ...that is why we need to manually direct the received messages to
                                            // the other diagramManager, that prior handled the message
                                            diagramManager.diagramConnector?.onMessageReceived(m);
                                            counter += 1;
                                            if (this.diagramManagers.length <= counter) {
                                                resolve();
                                            }
                                        });
                                        */ // TODO: SAMI: is onActionMessage still overwritten?
                                        resolve();
                                    })
                            )
                        );
                        console.log('registered: ' + MetaSpecificationReloadCommand.ID);
                        this.commandRegistry.executeCommand(MetaSpecificationReloadCommand.ID);
                    });
            });
        });
    }
}
