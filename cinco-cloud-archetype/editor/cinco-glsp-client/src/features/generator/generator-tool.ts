/********************************************************************************
 * Copyright (c) 2020-2022 Cinco Cloud and others.
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
import { GeneratorAction, GeneratorEditAction } from '@cinco-glsp/cinco-glsp-common';
import { Tool, TYPES, SelectionService, GLSPActionDispatcher } from '@eclipse-glsp/client';
import { inject, injectable, postConstruct } from 'inversify';

@injectable()
export class GeneratorTool implements Tool {
    static readonly ID = 'generator-tool';

    @inject(TYPES.IActionDispatcher) protected actionDispatcher: GLSPActionDispatcher;
    @inject(SelectionService) protected selectionService: SelectionService;

    @postConstruct()
    initGeneratorAction(): void {
        window.addEventListener('message', ({ data: message }: { data: GeneratorEditAction }) => {
            if (message.kind === 'cincoGenerate') {
                const currentModel = this.selectionService.getModelRoot();
                if (currentModel.id === message.modelElementId) {
                    const modelElementId = currentModel.id;
                    const action = GeneratorAction.create(modelElementId, message.targetFolder);
                    this.actionDispatcher.dispatch(action);
                }
            }
        });
    }

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    enable(): void {}

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    disable(): void {}

    get id(): string {
        return GeneratorTool.ID;
    }
}
