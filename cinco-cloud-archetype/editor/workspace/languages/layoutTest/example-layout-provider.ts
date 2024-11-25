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
 import { LanguageFilesRegistry, LayoutOptionsProvider } from '@cinco-glsp/cinco-glsp-api';
 import {
     LayoutOptionsRequestAction
 } from '@cinco-glsp/cinco-glsp-common';
 
 /**
  * Language Designer defined example of a LayoutOptionsProvider
  */
 export class ExampleLayoutOptionsProvider extends LayoutOptionsProvider {
     override CHANNEL_NAME: string | undefined = 'LayoutOptionsProvider [' + this.modelState.graphModel.id + ']';
 
     provide(action: LayoutOptionsRequestAction, ...args: unknown[]): Promise<string> | string {
         return `{
            "elk.algorithm": "random",
            "elk.spacing.nodeNode": 100
        }` // alternativly predefined values like in @label: `random`;
     }
 }
 // register into app
 LanguageFilesRegistry.register(ExampleLayoutOptionsProvider);
 