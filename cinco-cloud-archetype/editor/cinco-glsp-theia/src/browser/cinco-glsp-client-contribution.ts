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
import { BaseGLSPClientContribution } from '@eclipse-glsp/theia-integration/lib/browser';
import { EnvVariablesServer } from '@theia/core/lib/common/env-variables';
import { inject, injectable } from '@theia/core/shared/inversify';
import { getDiagramConfiguration } from '../common/cinco-language';
import {
    DIAGRAM_TYPE, MetaSpecification, MetaSpecificationReloadAction, MetaSpecificationReloadCommand, MetaSpecificationResponseAction
} from '@cinco-glsp/cinco-glsp-common';
import { CommandHandler, CommandRegistry } from '@theia/core';
import { Action, GLSPClient, ActionMessage } from '@eclipse-glsp/protocol';

@injectable()
export class CincoGLSPClientContribution extends BaseGLSPClientContribution {
    @inject(EnvVariablesServer)
    protected readonly envVariablesServer: EnvVariablesServer; // this could be used for env vars for connection
    @inject(CommandRegistry)
    protected readonly commandRegistry: CommandRegistry;

    readonly id = getDiagramConfiguration().contributionId;
    static readonly SYSTEM_ID = 'SYSTEM';

    constructor() {
        super();
        this.initializeSystemSession(CincoGLSPClientContribution.SYSTEM_ID);
    }

    initializeSystemSession(id: string): void {
        this.ready.then(client => {
            this.initialize(client).then(_v => {
                client.initializeClientSession({
                    clientSessionId: id,
                    diagramType: DIAGRAM_TYPE
                }).then(_v2 => {
                    this.commandRegistry.registerCommand(
                        { id: MetaSpecificationReloadCommand.ID, label: 'Reload Meta-Specification', category: 'Cinco Cloud' },
                        new MetaSpecificationReloadCommandHandler(client, (m: ActionMessage) => new Promise<void>(resolve => {
                                let counter = 0;
                                this.diagramManagerProviders.forEach(diagramManagerProvider => {
                                    diagramManagerProvider().then(diagramManager => {
                                        diagramManager.diagramConnector?.onMessageReceived(m);
                                        counter += 1;
                                        if(this.diagramManagerProviders.length <= counter) {
                                            resolve();
                                        }
                                    });
                                });
                            }))
                    );
                });
            });
        });
    }
}

class MetaSpecificationReloadCommandHandler implements CommandHandler {
    protected readonly client: GLSPClient;
    protected readonly callback: (m: ActionMessage) => Promise<void>;

    constructor(client: GLSPClient, callback: (m: ActionMessage) => Promise<void>) {
        this.client = client;
        this.callback = callback;
    }

    execute(...args: any[]): void {
        // request & reload metas pecification
        this.sendGLSPSystemAction(
            this.client,
            MetaSpecificationReloadAction.create([], true),
            response => {
                this.callback(response).then(_ => {
                    // update metaSpecification
                    if(response.action && response.action.kind === MetaSpecificationResponseAction.KIND) {
                        const metaSpecificationResponseAction = response.action as MetaSpecificationResponseAction;
                        const metaSpecification = metaSpecificationResponseAction.metaSpecification;
                        // MetaSpecification.clear();
                        MetaSpecification.clear();
                        MetaSpecification.merge(metaSpecification);
                        // update palette after meta-specification is updated
                        this.sendGLSPSystemAction(this.client, { kind: 'enableToolPalette'});
                    }
                });
            }
        );
    }

    sendGLSPSystemAction(
        client: GLSPClient, action: Action, callback?: (e: any) => void
    ): void {
        client.sendActionMessage({
            clientId: CincoGLSPClientContribution.SYSTEM_ID,
            action: action
        });
        if(callback) {
            client.onActionMessage(response => {
                callback(response);
            });
        }
    }
}
