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
 import { Node, LanguageFilesRegistry, Container, AbstractGraphModelHook, GraphModel } from '@cinco-glsp/cinco-glsp-api';
 import { PropertyEditOperation, AssignValue } from '@cinco-glsp/cinco-glsp-common';
 import { CreateNodeOperation, Dimension, Point } from '@eclipse-glsp/server';
 
 export class HooksAndActionsHook extends AbstractGraphModelHook {
     override CHANNEL_NAME: string | undefined = 'HooksAndActionsHook';
 
     /**
      * Create
      */
    canCreate(modelElementType: string, path: string): boolean {
        this.log('Triggered canCreate. Can create model of type (' + modelElementType + ') at path (' + path + ')');
        return true;
    }
    
    preCreate(modelElementType: string, path: string): void {
        this.log('Triggered preCreate. Creating model of type (' + modelElementType + ') at path (' + path + ')');
    }
 
     override postCreate(graphModel: GraphModel): void {
        this.log('Triggered postCreate on graphmodel (' + graphModel.id + ')');
     }

 }
 
 LanguageFilesRegistry.register(HooksAndActionsHook);
 