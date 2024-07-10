/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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
import { CreateEdgeOperation } from '@eclipse-glsp/server';
import { Edge, AbstractEdgeHooks, LanguageFilesRegistry, Node } from '@cinco-glsp/cinco-glsp-api';
import { AssignValue, PropertyEditOperation } from '@cinco-glsp/cinco-glsp-common';

export class TransitionHooks extends AbstractEdgeHooks {
    override CHANNEL_NAME: string | undefined = 'TransitionHooks [' + this.modelState.root.id + ']';

    override canCreate(operation: CreateEdgeOperation): boolean {
        this.log("Triggered canCreate. Can create edge of type: "+operation.elementTypeId);
        return true;
    }

    override preCreate(source: Node, target: Node): void {
        this.log("Triggered preCreate. Creating edge for source (" + source.id + ") and target (" + target.id + ")");        
    }

    override postCreate(edge: Edge): void {
        this.log("Triggered postCreate on edge (" + edge.id + ")");
    }

    override canDelete(edge: Edge): boolean {
        this.log("Triggered canDelete on edge (" + edge.id + ")");
        return true;
    }

    override preDelete(edge: Edge): void {
        this.log("Triggered preDelete on edge (" + edge.id + ")");
    }

    override postDelete(edge: Edge): void {
        this.log("Triggered postDelete on edge (" + edge.id + ")");
    }
    
    /**
     * Change Attribute
     */

    override canChangeAttribute(edge: Edge, operation: PropertyEditOperation): boolean {
        this.log("Triggered canChangeAttribute on edge ("+edge.id + ")");
        return operation.change.kind === 'assignValue';
    }


    override preAttributeChange(edge: Edge, operation: PropertyEditOperation): void {
        this.log("Triggered preAttributeChange on edge ("+edge.id + ")");
        this.log('Changing: ' + operation.name
            + ' from: ' + edge.getProperty(operation.name)
            + " to: "+ 
            (AssignValue.is(operation.change) ? operation.change.value : 'undefined'));
    }

    override postAttributeChange(edge: Edge, attributeName: string, oldValue: any): void {
        this.log("Triggered postAttributeChange on edge ("+edge.id + ")");
        this.log('Changed: ' + attributeName + ' from: ' + oldValue + " to: "+ edge.getProperty(attributeName));
    }

    /**
     * The following are not yet implemented
     */

    /**
     * Double Click
     */

    override canDoubleClick(edge: Edge): boolean {
        this.log("Triggered canDoubleClick on edge ("+edge.id + ")");
        return true;
    }

    override postDoubleClick(edge: Edge): void {
        this.log("Triggered postDoubleClick on edge ("+edge.id + ")");
    }

    /**
     * Select
     */

    override canSelect(edge: Edge): boolean {
        this.log("Triggered canSelect on edge ("+edge.id + ")");
        return true;
    }

    override postSelect(edge: Edge): boolean {
        this.log("Triggered postSelect on edge ("+edge.id + ")");
        return true;
    }
}

LanguageFilesRegistry.register(TransitionHooks);
