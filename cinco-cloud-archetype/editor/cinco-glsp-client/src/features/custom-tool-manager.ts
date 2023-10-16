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
import { IActionDispatcher, SModelRegistry, TYPES, Tool, ViewRegistry } from '@eclipse-glsp/client';
import { GLSPToolManager } from '@eclipse-glsp/client/lib/base/tool-manager/glsp-tool-manager';
import { CommandService } from '@theia/core';
import { inject, injectable, optional } from 'inversify';

@injectable()
export class CustomToolManager extends GLSPToolManager {
    @inject(CommandService) @optional() commandService: CommandService;
    @inject(TYPES.IActionDispatcherProvider) protected actionDispatcherProvider: () => Promise<IActionDispatcher>;

    // only those that are start
    @inject(TYPES.SModelRegistry)
    private readonly registry: SModelRegistry;
    @inject(TYPES.ViewRegistry)
    private readonly viewRegistry: ViewRegistry;

    protected deactivatedTools: string[] = ['glsp.change-bounds-tool', 'glsp.edge-edit-tool']; // put deactivated tool-ids here

    callbacks: ((actionDispatcher: IActionDispatcher, registry: SModelRegistry, viewRegistry: ViewRegistry) => void)[] = [];

    static oldRegistry: SModelRegistry;

    override initialize(): void {
        CustomToolManager.oldRegistry = this.registry;
        this.registerTools(...this.tools);
        this.tools = this.filterDeactivated(this.tools);
        this.defaultTools = this.filterDeactivated(this.defaultTools);
        this.registerDefaultTools(...this.defaultTools);
        this.enableDefaultTools();
        this.contextServiceProvider().then(editorContext => {
            editorContext.register(this);
            this.editorContext = editorContext;
        });

        // callbacks
        this.actionDispatcherProvider().then(actionDispatcher => {
            for (const callback of this.callbacks) {
                callback(actionDispatcher, this.registry, this.viewRegistry);
            }
            this.callbacks = [];
        });
    }

    filterDeactivated(tools: Tool[]): Tool[] {
        return tools.filter(tool => this.deactivatedTools.indexOf(tool.id) < 0);
    }

    /**
     * register a callback that is called, after the meta-specification is loaded.
     * @param callback
     */
    registerCallBack(callback: (actionDispatcher: IActionDispatcher, registry: SModelRegistry, viewRegistry: ViewRegistry) => void): void {
        this.callbacks.push(callback);
    }
}
