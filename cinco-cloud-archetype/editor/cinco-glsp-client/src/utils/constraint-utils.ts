/********************************************************************************
 * Copyright (c) 2022 Cinco Cloud.
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

import { Constraint, getSpecOf, isContainer, NodeType } from '@cinco-glsp/cinco-glsp-common';
import { GChildElement, GEdge, GGraph, GModelElement, GNode } from '@eclipse-glsp/client';

export function canBeEdgeTarget(target: GNode, edgeType: string, filter?: (e: GEdge) => boolean): boolean {
    const spec = getSpecOf(target.type) as NodeType;
    if (spec.incomingEdges === undefined) {
        return false;
    }
    const constraints: Constraint[] = spec.incomingEdges;
    if (constraints.length <= 0) {
        // cannot contain elements, if no relating constraints are defined
        return false;
    }
    const incomingEdges = target.incomingEdges;
    let elements = Array.from(incomingEdges ?? []);
    if (filter) {
        elements = elements.filter(e => filter(e));
    }
    return checkViolations(edgeType, elements, constraints).length <= 0;
}

export function canBeEdgeSource(source: GNode, edgeType: string, filter?: (e: GEdge) => boolean): boolean {
    const spec = getSpecOf(source.type) as NodeType;
    if (spec.outgoingEdges === undefined) {
        return false;
    }
    const constraints: Constraint[] = spec.outgoingEdges;
    if (constraints.length <= 0) {
        // cannot contain elements, if no relating constraints are defined
        return false;
    }
    const outgoingEdges = source.outgoingEdges;
    let elements = Array.from(outgoingEdges ?? []);
    if (filter) {
        elements = elements.filter(e => filter(e));
    }
    return checkViolations(edgeType, elements, constraints).length <= 0;
}

export function canContain(container: GNode | GGraph, containmentType: string, filter?: (e: GChildElement) => boolean): boolean {
    const containerSpec = getSpecOf(container.type) as NodeType;
    if (!isContainer(container.type)) {
        return false;
    }
    const constraints: Constraint[] = containerSpec.containments ? containerSpec.containments : [];
    if (constraints.length <= 0) {
        // cannot contain elements, if no relating constraints are defined
        return false;
    }
    let elements = container.children.filter(e => e instanceof GNode);
    if (filter) {
        elements = elements.filter(e => filter(e));
    }
    return checkViolations(containmentType, elements, constraints).length <= 0;
}

/**
 * Checks if a relation (IncomingEdge, OutgoingEdge, Containment) between this element and a target can be resolved,
 * in terms of its constraint. For this, the function checks if the upperbound of the constraint would be violated.
 * @param targetType elementType that will be checked
 * @param elements all current related elements
 * @param constraints all constraints associated with the target element
 * @returns All constraints that are violated. If there is no violated constraint, the target element can be related with this element.
 */
export function checkViolations(targetType: string, elements: GModelElement[], constraints: Constraint[]): Constraint[] {
    // all constraints that include the target type
    const capturedConstraints = constraints.filter(
        (c: Constraint) =>
            // elements type is captured by the constraint
            (c.elements ?? []).indexOf(targetType) >= 0
    );
    // if element is not contained in any constrained, it violates all constraints
    if (capturedConstraints.length <= 0) {
        return constraints;
    }
    // out of all constraints that include the targets type...
    const violatedConstraint = capturedConstraints.filter(c => {
        // ...check if those constraints are already met,...
        // ...i.e. the upperbound is met by the sum of all related and captured types
        let upperBound: number = c.upperBound;
        // if upperBound is initially smaller than 0, it is interpreted as wildcard '*'
        if (upperBound < 0) {
            return false;
        }
        for (const type of c.elements ?? []) {
            const connectedNumberOfCapturedType: number = elements.filter(e => e.type === type).length;
            upperBound = upperBound - connectedNumberOfCapturedType;
            if (upperBound <= 0) {
                // if it is met, the constraint would be violated
                return true;
            }
        }
        // if the upperBound is not met, the constraint won't be violated
        return false;
    });
    // the function returns all violated constraints
    return violatedConstraint;
}

export function isContainableByConstraints(container: GModelElement, element: string | GModelElement): boolean {
    if (container instanceof GNode || container instanceof GGraph) {
        if (element instanceof GChildElement) {
            // already contained or check type
            return container.children.indexOf(element) >= 0 || isContainableByConstraints(container, element.type);
        } else if (typeof element === 'string') {
            // check if type can be contained
            return canContain(container, element);
        }
    }
    return false;
}
