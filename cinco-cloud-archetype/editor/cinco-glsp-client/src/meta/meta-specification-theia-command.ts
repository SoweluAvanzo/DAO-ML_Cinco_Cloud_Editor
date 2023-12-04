/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud and others.
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
import { MetaSpecificationReloadAction, MetaSpecificationReloadCommand } from '@cinco-glsp/cinco-glsp-common';
import { GLSPActionDispatcher, Tool, TYPES } from '@eclipse-glsp/client';
import { CommandService } from '@theia/core';
import { inject, injectable, optional, postConstruct } from 'inversify';
import { GraphModelProvider } from '../model/graph-model-provider';

@injectable()
export class MetaSpecificationTheiaCommand implements Tool {
    @inject(CommandService) @optional() commandService: CommandService;
    @inject(GraphModelProvider)
    protected readonly graphModelProvider: GraphModelProvider;
    @inject(TYPES.IActionDispatcher) protected actionDispatcher: GLSPActionDispatcher;

    static readonly ID = 'meta-specification-theia-command-tool';

    @postConstruct()
    registerTheiaCommand(): void {
        if (this.commandService && this.graphModelProvider) {
            this.graphModelProvider.graphModel.then(model => {
                const filePath = model.id; // TODO: put workspace-file-path here
                this.commandService.executeCommand('registerFromGLSP2Theia', {
                    commandId: MetaSpecificationReloadCommand.ID + '.' + model.id,
                    instanceId: model.id,
                    visible: true,
                    label: 'Reload Meta-Specification current model (' + filePath + ')',
                    callbacks: [
                        () => {
                            this.actionDispatcher.dispatch(MetaSpecificationReloadAction.create([], true));
                        }
                    ]
                });
            });
        }
    }

    get id(): string {
        return MetaSpecificationTheiaCommand.ID;
    }

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    enable(): void {}

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    disable(): void {}
}
