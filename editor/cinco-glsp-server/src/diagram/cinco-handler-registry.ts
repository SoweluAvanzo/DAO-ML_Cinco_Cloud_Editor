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

import { OperationHandler, OperationHandlerRegistry, CreateOperationHandler, Operation, CreateOperation } from '@eclipse-glsp/server';
import { injectable, multiInject } from 'inversify';
import { SpecifiedNodeHandler } from '../handler/specified_node_handler';
import { SpecifiedEdgeHandler } from '../handler/specified_edge_handler';

@injectable()
export class CincoOperationHandlerRegistry extends OperationHandlerRegistry {
    @multiInject(SpecifiedNodeHandler)
    protected nodeHandler: SpecifiedNodeHandler[];
    @multiInject(SpecifiedEdgeHandler)
    protected edgeHandler: SpecifiedNodeHandler[];

    /**
     * Override: Allowing reregistration
     */
    override register(key: string, value: OperationHandler): boolean {
        this.elements.set(key, value);
        return true;
    }

    override registerHandler(handler: OperationHandler): boolean {
        if (CreateOperationHandler.is(handler)) {
            handler.elementTypeIds.forEach(typeId => this.register(`${handler.operationType}_${typeId}`, handler));
            return true;
        } else {
            return this.register(handler.operationType, handler);
        }
    }

    override getOperationHandler(operation: Operation): OperationHandler | undefined {
        this.updateCreateHandler();
        return CreateOperation.is(operation) ? this.get(`${operation.kind}_${operation.elementTypeId}`) : this.get(operation.kind);
    }

    /**
     * TODO: this could be more optimized. Currently everytime the operationHandler get checked, they are updated.
     * For potencially large graphical DSL this can get expensive. The reason for this short cut is,
     * that this class is in reality only a singleton for a client connection, but not a singleton inside the server.
     * Each of these registries should be updated globally, but the structure of the GLSP does not conclude, where they can be updated.
     */
    updateCreateHandler(): void {
        for (const h of this.nodeHandler) {
            this.registerHandler(h);
        }
        for (const h of this.edgeHandler) {
            this.registerHandler(h);
        }
    }

    override get(key: string): OperationHandler | undefined {
        return this.elements.get(key);
    }
}
