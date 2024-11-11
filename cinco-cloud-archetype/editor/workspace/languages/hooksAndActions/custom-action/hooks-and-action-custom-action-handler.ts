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
 import { CustomActionHandler, LanguageFilesRegistry, ModelElement, Node } from '@cinco-glsp/cinco-glsp-api';
 import { Action, CustomAction } from '@cinco-glsp/cinco-glsp-common';
 
 /**
  * Language Designer defined example of a SelectHandler
  */
 export class HooksAndActionsCustomActionHandler extends CustomActionHandler {
     override CHANNEL_NAME: string | undefined = 'HooksAndActions [' + this.modelState.graphModel.id + ']';
 
     override execute(action: CustomAction, ...args: unknown[]): Promise<Action[]> | Action[] {
         // parse action
         const modelElementId: string = action.modelElementId;
         const element = this.modelState.index.findElement(modelElementId)! as ModelElement;
 
         // logging
         const message = 'Executing custom action for Element [' + element.type + '] with id: ' + element.id;
         this.log(message, { show: true });
 
         return [];
     }
 
     override canExecute(action: CustomAction, ...args: unknown[]): Promise<boolean> | boolean {
         return false;
     }
 }
 // register into app
 LanguageFilesRegistry.register(HooksAndActionsCustomActionHandler);
 