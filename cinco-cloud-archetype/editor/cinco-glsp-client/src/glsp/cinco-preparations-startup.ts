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
import {
    IActionDispatcher,
    IDiagramOptions,
    IDiagramStartup,
    IViewerProvider,
    Ranked,
    SModelRegistry,
    TYPES,
    TypeHintProvider,
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
import { EnvironmentProvider, IEnvironmentProvider } from '../api/environment-provider';
import { CincoEdge, CincoNode } from '../model/model';

@injectable()
export class CinoPreparationsStartUp implements IDiagramStartup, Ranked {
    static _rank: number = CINCO_STARTUP_RANK;
    rank: number = CinoPreparationsStartUp._rank;
    @inject(EnvironmentProvider) environmentProvider: IEnvironmentProvider;
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
    @inject(TYPES.IDiagramOptions)
    protected options: IDiagramOptions;
    @inject(TYPES.IViewerProvider)
    @optional()
    protected readonly viewerProvider: IViewerProvider;
    @inject(TypeHintProvider)
    protected typeHintProvider: TypeHintProvider;

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

        await MetaSpecificationLoader.load(this.actionDispatcher, this.environmentProvider);
        this.prepareAfterMetaSpecification();
        MetaSpecificationResponseHandler.addRegistrationCallback(clientId, () => {
            if (!client.isConnected(clientId) && !client.isConnectingOrRunning()) {
                MetaSpecificationResponseHandler.removeRegistrationCallback(clientId);
            } else {
                this.prepareAfterMetaSpecification(true);
            }
        });
        await this.environmentProvider.postRequestMetaSpecification();
    }

    prepareAfterMetaSpecification(updateCanvas: boolean = false): void {
        // load server args
        ServerArgsProvider.load(this.actionDispatcher);
        // load css language-files
        FrontendResourceLoader.load(this.actionDispatcher, this.workspaceFileService);
        // dynamically register bindings
        try {
            // TODO: this call fires a "no matching bindings found for serviceIdentifier: EdgeRouterRegistry"
            reregisterBindings(this.context, this.ctx, this.registry, this.viewRegistry);
        } catch (e) {
            console.log(e);
        }
        // dynamic tool palette update
        CincoToolPalette.requestPalette(this.actionDispatcher);
        // update canvas
        if (updateCanvas) {
            this.graphModelProvider.graphModel.then(g => {
                g.children.forEach(c => {
                    if (c instanceof CincoNode) {
                        c.reset();
                    } else if (c instanceof CincoEdge) {
                        c.reset();
                    }
                });
                this.viewerProvider.modelViewer.update(g);
                this.typeHintProvider.postRequestModel();
            });
        }
    }

    async postRequestModel?(): Promise<void> {
        // wait for graphmodel to be loaded, to prevent race-conditions
        await this.graphModelProvider.graphModel;
        CincoToolPalette.requestPalette(this.actionDispatcher);
    }
}
