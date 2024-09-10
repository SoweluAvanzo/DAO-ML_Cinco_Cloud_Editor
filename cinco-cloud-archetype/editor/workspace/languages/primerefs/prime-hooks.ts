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
 import { Node, Edge, AbstractNodeHook, LanguageFilesRegistry, GraphModel, Container } from '@cinco-glsp/cinco-glsp-api';

 export class PrimeHooks extends AbstractNodeHook {
     override CHANNEL_NAME: string | undefined = 'PrimeHooks [' + this.modelState.root.id + ']';
 
     /**
      * Double Click
      */
 
     override canDoubleClick(node: Node): boolean {
        if(node.isPrime) {
            this.log('Triggered node is a primeNode!');
        } else {
            this.log('Triggered node is not a primeNode!');
        }
        return true;
     }
 
     override postDoubleClick(node: Node): void {
        if(node.isPrime) {
            const reference = node.primeReference!;
            const referenceInfo = node.primeReferenceInfo!;
            this.log('Resolved Reference:\n' + JSON.stringify(reference));
            if(reference instanceof GraphModel) {
                this.log('Triggered node references a: GraphModel');
            } else if(reference instanceof Container) {
                this.log('Triggered node references a: Container');
            } else if(reference instanceof Node) {
                this.log('Triggered node references a: Node');
            } else if(reference instanceof Edge) {
                this.log('Triggered node references a: Edge');
            } 
            this.log('Read Referenced Model from: '+referenceInfo.filePath);
            this.log('(Beware! The filePath is a last known location, but the value is unsafe!)');
        } else {
            this.log('Triggered node is not a primeNode!');
        }
     }
 }
 
 LanguageFilesRegistry.register(PrimeHooks);
 