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
import { TYPES, Tool, EditorContextServiceProvider } from '@eclipse-glsp/client';
import { ToolManager } from '@eclipse-glsp/client/lib/base/tool-manager/tool-manager';
import { inject, injectable, multiInject, optional } from 'inversify';

@injectable()
export class CincoToolManager extends ToolManager {
    @multiInject(TYPES.ITool)
    @optional()
    override readonly tools: Tool[] = [];
    @multiInject(TYPES.IDefaultTool)
    @optional()
    override readonly defaultTools: Tool[];
    @inject(TYPES.IEditorContextServiceProvider) protected override contextServiceProvider: EditorContextServiceProvider;

    protected deactivatedTools: string[] = ['glsp.change-bounds-tool', 'glsp.edge-edit-tool']; // put deactivated tool-ids here

    constructor(tools: Tool[], defaultTools: Tool[], contextServiceProvider: EditorContextServiceProvider) {
        super([], [], contextServiceProvider);
        this.tools = tools;
        this.defaultTools = defaultTools;
        this.contextServiceProvider = contextServiceProvider;

        this.tools = this.filterDeactivated(this.tools);
        this.defaultTools = this.filterDeactivated(this.defaultTools);
        this.registerTools(...this.tools);
        this.registerDefaultTools(...this.defaultTools);
        this.enableDefaultTools();
    }

    filterDeactivated(tools: Tool[]): Tool[] {
        return tools.filter(tool => this.deactivatedTools.indexOf(tool.id) < 0);
    }
}
