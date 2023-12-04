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
import { CommandService } from '@theia/core';
import {
    IActionDispatcher,
    ICommandStack,
    IDiagramOptions,
    IDiagramStartup,
    Ranked,
    SModelRegistry,
    TYPES,
    ViewRegistry
} from '@eclipse-glsp/client';
import { MetaSpecificationLoader } from '../meta/meta-specification-loader';
import { MetaSpecificationResponseHandler } from '../meta/meta-specification-response-handler';
import { inject, injectable, optional } from 'inversify';
import { ServerArgsProvider } from '../meta/server-args-response-handler';
import { FrontendResourceLoader } from '../meta/frontend-resource.loader';
import { reregisterBindings } from '../meta/meta-model-glsp-registration-handler';
import { WorkspaceFileService } from '../utils/workspace-file-service';
import { GraphModelProvider } from '../model/graph-model-provider';
import { CincoToolPalette } from './cinco-tool-palette';
import { CincoGLSPClient, CINCO_STARTUP_RANK } from '@cinco-glsp/cinco-glsp-common';

@injectable()
export class CinoPreparationsStartUp implements IDiagramStartup, Ranked {
    static _rank: number = CINCO_STARTUP_RANK;
    rank: number = CinoPreparationsStartUp._rank;
    @inject(CommandService) @optional() commandService?: CommandService;
    @inject(WorkspaceFileService)
    protected readonly workspaceFileService: WorkspaceFileService;
    @inject(TYPES.SModelRegistry)
    protected readonly registry: SModelRegistry;
    @inject(TYPES.ViewRegistry)
    protected readonly viewRegistry: ViewRegistry;
    @inject(GraphModelProvider)
    protected readonly graphModelProvider: GraphModelProvider;
    @inject(TYPES.IActionDispatcher)
    protected readonly actionDispatcher: IActionDispatcher;
    @inject(TYPES.ICommandStack)
    protected readonly commandStack: ICommandStack;
    @inject(CincoToolPalette)
    protected readonly palette: CincoToolPalette;
    @inject(TYPES.IDiagramOptions)
    protected options: IDiagramOptions;

    protected context: {
        bind: any;
        isBound: any;
    };
    protected ctx: any;

    setContext(
        context: {
            bind: any;
            isBound: any;
        },
        ctx: any
    ): void {
        this.context = context;
        this.ctx = ctx;
    }

    async preRequestModel?(): Promise<void> {
        const client = await this.options.glspClientProvider();
        if (!(client instanceof CincoGLSPClient)) {
            throw new Error('Client is no CincoGLSPClient. The API must have change. Please review!');
        }
        const clientId = this.options.clientId;
        MetaSpecificationResponseHandler.addRegistrationCallback(clientId, () => {
            if (!client.isConnected(clientId)) {
                MetaSpecificationResponseHandler.removeRegistrationCallback(clientId);
            } else {
                this.prepareAfterMetaSpecification();
            }
        });
        await MetaSpecificationLoader.load(this.actionDispatcher, this.commandService);
    }

    prepareAfterMetaSpecification(): void {
        // load server args
        ServerArgsProvider.load(this.actionDispatcher);
        // load css language-files
        FrontendResourceLoader.load(this.actionDispatcher, this.workspaceFileService);
        // dynamically register bindings
        reregisterBindings(this.context, this.ctx, this.registry, this.viewRegistry);
        // dynamic tool palette update
        this.palette.requestPalette();
    }

    async postRequestModel?(): Promise<void> {
        // wait for graphmodel to be loaded, to prevent race-conditions
        await this.graphModelProvider.graphModel;
    }
}
