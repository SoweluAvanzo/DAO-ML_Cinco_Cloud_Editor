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
import { IActionDispatcher, IDiagramStartup, MaybePromise, SModelRegistry, TYPES, ToolPalette, ViewRegistry } from '@eclipse-glsp/client';
import { MetaSpecificationLoader } from './meta-specification-loader';
import { MetaSpecificationResponseHandler } from './meta-specification-response-handler';
import { inject, injectable } from 'inversify';
import { ServerArgsProvider } from './server-args-response-handler';
import { DynamicImportLoader } from './dynamic-import-tool';
import { reregisterBindings } from './meta-model-glsp-registration-handler';
import { WorkspaceFileService } from '../utils/workspace-file-service';
import { DynamicToolPalette } from '../features/dynamic-palette-tool';

@injectable()
export class MetaModelStartUp implements IDiagramStartup {
    @inject(TYPES.IActionDispatcher)
    protected readonly actionDispatcher: IActionDispatcher;
    @inject(WorkspaceFileService)
    protected readonly workspaceFileService: WorkspaceFileService;
    @inject(TYPES.SModelRegistry)
    protected readonly registry: SModelRegistry;
    @inject(TYPES.ViewRegistry)
    protected readonly viewRegistry: ViewRegistry;
    @inject(ToolPalette)
    protected readonly palette: ToolPalette;

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

    preRequestModel?(): MaybePromise<void> {
        MetaSpecificationResponseHandler.addRegistrationCallback(() => this.reloadMetaModel());
        MetaSpecificationLoader.load(this.actionDispatcher);
    }

    reloadMetaModel(): void {
        // load server args
        ServerArgsProvider.load(this.actionDispatcher);
        // load css language-files
        DynamicImportLoader.load(this.actionDispatcher, this.workspaceFileService);
        // dynamically register bindings
        reregisterBindings(this.context, this.ctx, this.registry, this.viewRegistry);
    }

    postRequestModel?(): MaybePromise<void> {
        // update palette
        if (this.palette instanceof DynamicToolPalette) {
            this.palette.requestPalette();
        }
    }
}
