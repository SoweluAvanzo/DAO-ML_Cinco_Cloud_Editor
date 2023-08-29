import { /*ValidationAcceptor,*/ ValidationAcceptor, ValidationChecks } from 'langium';
import { CincoAstType, Edge, EdgeElementConnection, ModelElement, NodeType } from '../../generated/ast';
import type { MglServices } from './mgl-module';
import { getConnectingEdges } from '../util/mgl-util';

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: MglServices) {
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

}
