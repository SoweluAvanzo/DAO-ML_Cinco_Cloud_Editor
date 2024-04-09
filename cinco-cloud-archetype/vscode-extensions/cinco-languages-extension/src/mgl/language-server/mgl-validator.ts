import { /*ValidationAcceptor,*/ ValidationAcceptor, ValidationChecks } from 'langium';
import { CincoAstType, Edge, EdgeElementConnection, ModelElement, ComplexModelElement, NodeType } from '../../generated/ast';
import type { MglServices } from './mgl-module';
import { getConnectingEdges } from '../util/mgl-util';

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: MglServices) {
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

    checkModelElementNameNotUnique(modelElement: ModelElement, acceptor: ValidationAcceptor) {
        for(const otherElement of modelElement.$container.modelElements) {
            if(modelElement !== otherElement && modelElement.name === otherElement.name) {
                acceptor('error', 'Names of model elements have to be unique', { node: modelElement, property: 'name' });
            }
        }
	}

    checkIncomingEdgesUnique(nodeType: NodeType, acceptor: ValidationAcceptor) {
        const incomingEdges = nodeType.incomingEdgeConnections.flatMap(
            (incomingConnection: EdgeElementConnection) => getConnectingEdges(incomingConnection)
            );
        for(const incomingEdge of incomingEdges) {
            const filteredList = incomingEdges.filter((edge: Edge) => {return incomingEdge.name === edge.name});
            if(filteredList.length > 1) {
                acceptor('error', 'Incoming edges should be unique', { node: incomingEdge, property: 'name' });
            }
        }
    }

    checkOutgoingEdgesUnique(nodeType: NodeType, acceptor: ValidationAcceptor) {
        const outgoingEdges = nodeType.outgoingEdgeConnections.flatMap(
            (outgoingConnection: EdgeElementConnection) => getConnectingEdges(outgoingConnection)
            );
        for(const outgoingEdge of outgoingEdges) {
            if(outgoingEdges.filter((edge: Edge) => outgoingEdge.name === edge.name).length > 1) {
                acceptor('error', 'Outgoing edges should be unique', { node: outgoingEdge, property: 'name' });
            }
        }
    }

    checkInheritedAttributes(modelElement: ComplexModelElement, acceptor: ValidationAcceptor) {
        if (modelElement.defaultValueOverrides) {
            for (const defaultValueOverride of modelElement.defaultValueOverrides) {
                // Check if DefaultValueOverride references an AttributeDefinition from an inherited node
                const attributeName = defaultValueOverride.attribute
                if (!this.isDefinedAttribute(modelElement, attributeName)) {
                    acceptor('error', "Overriding Attribute name '$attributeName' is not a valid local or inherited attribute.", { node: defaultValueOverride, property: 'attribute' })
                }
            }
        }
    } 

    isDefinedAttribute(modelElement: ComplexModelElement, attributeName: string): boolean {
        // Check if the attribute is defined locally in this node
        if (modelElement.attributes.filter(a => a.name == attributeName).length > 0) {
            return true
        }
        // Check if the attribute is inherited from parent nodes
        if (modelElement.localExtension) {
            const parent = modelElement.localExtension.ref;
            if(parent) {
                return this.isDefinedAttribute(parent, attributeName)
            }
        } else if (modelElement.externalExtension) {
            // TODO: resolve
			return true
		}
		// not extending, no attributes with the name
        return false
    }
}
