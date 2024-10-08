/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
import { DoubleClickHandler, LanguageFilesRegistry, ModelElement, Node } from '@cinco-glsp/cinco-glsp-api';
import { Action, DoubleClickAction } from '@cinco-glsp/cinco-glsp-common';

/**
 * Language Designer defined example of a DoubleClickHandler
 */
export class ExampleDoubleClickHandler extends DoubleClickHandler {
    override CHANNEL_NAME: string | undefined = 'Example [' + this.modelState.graphModel.id + ']';

    override execute(action: DoubleClickAction, ...args: unknown[]): Promise<Action[]> | Action[] {
        // parse action
        const modelElementId: string = action.modelElementId;
        const element = this.modelState.index.findElement(modelElementId)! as ModelElement;

        // logging
        const message = 'Element [' + element.type + '] was double-clicked with id: ' + element.id;
        this.log(message, { show: true });
        this.dialog('DoubleClickEvent!', message, { ok: 'Yes', cancel: 'No' }).then(dialogResult => {
            const buttonText = dialogResult === 'true' ? 'Yes' : 'No';
            this.notify('You clicked: ' + buttonText);

            this.saveModel();
            this.notify('saved Model');
            this.submitModel();
            this.notify('submitted Model');
        });

        // next actions => find all activities and update their appearance
        const consecutiveActions: Action[] = [];
        return consecutiveActions;
    }

    override canExecute(action: DoubleClickAction, ...args: unknown[]): Promise<boolean> | boolean {
        return true;
    }
}
// register into app
LanguageFilesRegistry.register(ExampleDoubleClickHandler);
