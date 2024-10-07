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
import { LanguageFilesRegistry, ValidationHandler } from '@cinco-glsp/cinco-glsp-api';
import { Action, ValidationResponseAction, ValidationRequestAction, ValidationStatus } from '@cinco-glsp/cinco-glsp-common';

export class HooksAndActionsValidator extends ValidationHandler {
    override CHANNEL_NAME: string | undefined = 'HooksAndActions [' + this.modelState.root.id + ']';

    override execute(action: ValidationRequestAction, ...args: unknown[]): Promise<Action[]> | Action[] {
        // next actions

        const modelElement = this.getElement(action.modelElementId);
        const name = `${modelElement.getSpec().label} (${modelElement.id})`
        return [ValidationResponseAction.create(
            this.modelState.graphModel.id,
            action.modelElementId,
            [
                modelElement.type == 'hooksandactions:hooksandactions' ? 
                {
                    name: name,
                    message: 'Element is valid.',
                    status: ValidationStatus.Pass
                } :
                {
                    name: name,
                    message: 'Element has errors.',
                    status: ValidationStatus.Error
                }
                
            ],
            action.requestId
        )];
    }

    override canExecute(action: ValidationRequestAction, ...args: unknown[]): Promise<boolean> | boolean {
        const element = this.getElement(action.modelElementId);
        return element !== undefined;
    }
}
// register into app
LanguageFilesRegistry.register(HooksAndActionsValidator);
