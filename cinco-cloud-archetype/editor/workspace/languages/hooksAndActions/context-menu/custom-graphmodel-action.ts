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

 import { CustomActionHandler, LanguageFilesRegistry, Node } from '@cinco-glsp/cinco-glsp-api';
 import { Action, CustomAction } from '@cinco-glsp/cinco-glsp-common';
 
export class CustomGraphModelAction extends CustomActionHandler {
     
    override execute(action: CustomAction, ...args: any): Promise<Action[]> | Action[] {
        const selectedElements: Node[] = action.selectedElementIds
            .map(e => this.modelState.index.findNode(e))
            .filter(e => e !== undefined) as Node[];
        this.dialog('Custom Graphmodel Action', "Triggered Custom GraphModelAction!");
        return [];
    }
 
    override canExecute(action: CustomAction, ...args: unknown[]): boolean | Promise<boolean> {
        return action.selectedElementIds.length >= 1;
    }
}
 
LanguageFilesRegistry.register(CustomGraphModelAction);
 