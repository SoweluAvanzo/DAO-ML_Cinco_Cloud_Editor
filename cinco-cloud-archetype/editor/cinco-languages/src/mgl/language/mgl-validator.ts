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
import { CincoAstType, Edge, EdgeElementConnection, ModelElement, ComplexModelElement, NodeType } from '../../generated/ast';
import type { MglServices } from './mgl-module';
import { getConnectingEdges } from '../util/mgl-util';

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: MglServices): void {
    const registry = services.validation.ValidationRegistry;
    const validator = services.validation.MglValidator;
    const checks: ValidationChecks<CincoAstType> = {
        ModelElement: [
            validator.checkModelElementNameNotUnique,
            validator.checkInheritedAttributes
        ],
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
        for(const otherElement of modelElement.$container.modelElements) {
            if(modelElement !== otherElement && modelElement.name === otherElement.name) {
                acceptor('error', 'Names of model elements have to be unique', { node: modelElement, property: 'name' });
            }
        }
	}

    checkIncomingEdgesUnique(nodeType: NodeType, acceptor: ValidationAcceptor): void {
        const incomingEdges = nodeType.incomingEdgeConnections.flatMap(
            (incomingConnection: EdgeElementConnection) => getConnectingEdges(incomingConnection)
            );
        for(const incomingEdge of incomingEdges) {
            const filteredList = incomingEdges.filter((edge: Edge) => incomingEdge.name === edge.name);
            if(filteredList.length > 1) {
                acceptor('error', 'Incoming edges should be unique', { node: incomingEdge, property: 'name' });
            }
        }
    }

    checkOutgoingEdgesUnique(nodeType: NodeType, acceptor: ValidationAcceptor): void {
        const outgoingEdges = nodeType.outgoingEdgeConnections.flatMap(
            (outgoingConnection: EdgeElementConnection) => getConnectingEdges(outgoingConnection)
            );
        for(const outgoingEdge of outgoingEdges) {
            if(outgoingEdges.filter((edge: Edge) => outgoingEdge.name === edge.name).length > 1) {
                acceptor('error', 'Outgoing edges should be unique', { node: outgoingEdge, property: 'name' });
            }
        }
    }

    checkInheritedAttributes(modelElement: ComplexModelElement, acceptor: ValidationAcceptor): void {
        if (modelElement.defaultValueOverrides) {
            for (const defaultValueOverride of modelElement.defaultValueOverrides) {
                // Check if DefaultValueOverride references an AttributeDefinition from an inherited node
                const attributeName = defaultValueOverride.attribute;
                if (!this.isDefinedAttribute(modelElement, attributeName)) {
                    acceptor('error',
                    'Overriding Attribute is not a valid local or inherited attribute.',
                    { node: defaultValueOverride, property: 'attribute' });
                }
                if(modelElement.defaultValueOverrides.filter(d => d.attribute === attributeName).length > 1) {
                    acceptor('error', 'Overriding Attribute is a duplicate.', { node: defaultValueOverride, property: 'attribute' });
                }
            }
            for(const attribute of modelElement.attributes) {
                // Check if DefaultValueOverride references an AttributeDefinition from an inherited node
                const attributeName = attribute.name;
                if (this.isDefinedAttribute(modelElement, attributeName, true)) {
                    acceptor('error',
                    'Attribute is a duplicate. '
                    + "It is either defined locally or an inherited attribute.Use 'override <attributeName>' to override it.",
                    { node: attribute, property: 'name' });
                }
            }
        }
    }

    isDefinedAttribute(modelElement: ComplexModelElement, attributeName: string, checkDuplicate = false): boolean {
        // Check if the attribute is defined locally in this node
        if (modelElement.attributes.filter(a => a.name === attributeName).length > (checkDuplicate ? 1 : 0)) {
            return true;
        }
        // Check if the attribute is inherited from parent nodes
        if (modelElement.localExtension) {
            const parent = modelElement.localExtension.ref;
            if(parent) {
                return this.isDefinedAttribute(parent, attributeName);
            }
        } else if (modelElement.externalExtension) {
            // TODO: resolve
			return true;
		}
		// not extending, no attributes with the name
        return false;
    }
}
