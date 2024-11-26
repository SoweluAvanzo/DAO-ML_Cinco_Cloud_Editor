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
 import { LanguageFilesRegistry, AbstractGraphModelHook, GraphModel } from '@cinco-glsp/cinco-glsp-api';
import { LayoutArgument } from '@cinco-glsp/cinco-glsp-common';
 
 export class LayoutHooks extends AbstractGraphModelHook {
     override CHANNEL_NAME: string | undefined = 'LayoutHooks';
 
    // Layout
    canLayout(modelElement: GraphModel, parameter: LayoutArgument): boolean {
        return true;
    }
    preLayout(modelElement: GraphModel, parameter: LayoutArgument): void {
        this.log("Beginning layouting of: "+modelElement.id);
    }
    postLayout(modelElement: GraphModel, parameter: LayoutArgument): void {
        this.log("Layouted: "+modelElement.id);
    }
 }
 
 LanguageFilesRegistry.register(LayoutHooks);
 