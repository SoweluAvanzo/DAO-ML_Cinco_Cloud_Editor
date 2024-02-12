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
import { /* ValidationAcceptor,*/ ValidationAcceptor, ValidationChecks } from 'langium';
import { CincoAstType, Edge, EdgeElementConnection, ModelElement, NodeType } from '../../generated/ast.js';
import type { MglServices } from './mgl-module.js';
import { getConnectingEdges } from '../util/mgl-util.js';

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: MglServices): void {
    const registry = services.validation.ValidationRegistry;
    const validator = services.validation.MglValidator;
    const checks: ValidationChecks<CincoAstType> = {
        ModelElement: validator.checkModelElementNameNotUnique,
        NodeType: [
            // validator.checkIncomingEdgesUnique,
            // validator.checkOutgoingEdgesUnique
        ]
    };
    registry.register(checks, validator);
}

/**
 * Implementation of custom validations.
 */
export class MglValidator {
    checkModelElementNameNotUnique(modelElement: ModelElement, acceptor: ValidationAcceptor): void {
        for (const otherElement of modelElement.$container.modelElements) {
            if (modelElement !== otherElement && modelElement.name === otherElement.name) {
                acceptor('error', 'Names of model elements have to be unique', { node: modelElement, property: 'name' });
            }
        }
    }

    checkIncomingEdgesUnique(nodeType: NodeType, acceptor: ValidationAcceptor): void {
        const incomingEdges = nodeType.incomingEdgeConnections.flatMap((incomingConnection: EdgeElementConnection) =>
            getConnectingEdges(incomingConnection)
        );
        for (const incomingEdge of incomingEdges) {
            const filteredList = incomingEdges.filter((edge: Edge) => incomingEdge.name === edge.name);
            if (filteredList.length > 1) {
                acceptor('error', 'Incoming edges should be unique', { node: incomingEdge, property: 'name' });
            }
        }
    }

    checkOutgoingEdgesUnique(nodeType: NodeType, acceptor: ValidationAcceptor): void {
        const outgoingEdges = nodeType.outgoingEdgeConnections.flatMap((outgoingConnection: EdgeElementConnection) =>
            getConnectingEdges(outgoingConnection)
        );
        for (const outgoingEdge of outgoingEdges) {
            if (outgoingEdges.filter((edge: Edge) => outgoingEdge.name === edge.name).length > 1) {
                acceptor('error', 'Outgoing edges should be unique', { node: outgoingEdge, property: 'name' });
            }
        }
    }
}
