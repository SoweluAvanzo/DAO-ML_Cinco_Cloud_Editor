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
import { DIAGRAM_TYPE, MetaSpecificationReloadCommand } from '@cinco-glsp/cinco-glsp-common';
import { CommandRegistry, SelectionService } from '@theia/core';
import { ActionMessage } from '@eclipse-glsp/protocol';
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { LabelProvider, OpenerService } from '@theia/core/lib/browser';
import { MetaSpecificationReloadCommandHandler } from './meta/meta-specification-reload-command-handler';

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

    readonly id = getDiagramConfiguration().contributionId;
    static readonly SYSTEM_ID = 'SYSTEM';

    constructor() {
        super();
        this.initializeSystemSession(CincoGLSPClientContribution.SYSTEM_ID);
    }

    initializeSystemSession(id: string): void {
        this.ready.then(client => {
            this.initialize(client).then(_v => {
                client
                    .initializeClientSession({
                        clientSessionId: id,
                        diagramType: DIAGRAM_TYPE
                    })
                    .then(_v2 => {
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
                                        let counter = 0;
                                        this.diagramManagerProviders.forEach(diagramManagerProvider => {
                                            diagramManagerProvider().then(diagramManager => {
                                                // ...that is why we need to manually direct the received messages to
                                                // the other diagramManager, that prior handled the message
                                                diagramManager.diagramConnector?.onMessageReceived(m);
                                                counter += 1;
                                                if (this.diagramManagerProviders.length <= counter) {
                                                    resolve();
                                                }
                                            });
                                        });
                                    })
                            )
                        );
                        this.commandRegistry.executeCommand(MetaSpecificationReloadCommand.ID);
                    });
            });
        });
    }
}
