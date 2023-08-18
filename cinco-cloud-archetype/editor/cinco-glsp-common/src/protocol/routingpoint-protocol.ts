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
import { Point } from '../meta-specification';
import { Operation } from './shared-protocol';
import { hasArrayProp, hasStringProp } from './type-utils';

export class RoutingPoint implements Point {
    x: number;
    y: number;
}

export namespace ChangeRoutingPointsOperation {
    export const KIND = 'changeRoutingPoints';

    export function is(object: any): object is ChangeRoutingPointsOperation {
        return Operation.hasKind(object, KIND) && hasArrayProp(object, 'newRoutingPoints');
    }

    export function create(newRoutingPoints: ElementAndRoutingPoints[]): ChangeRoutingPointsOperation {
        return {
            kind: KIND,
            isOperation: true,
            newRoutingPoints
        };
    }
}
export interface ChangeRoutingPointsOperation extends Operation {
    kind: typeof ChangeRoutingPointsOperation.KIND;

    /**
     * The routing points of the edge (may be empty).
     */
    newRoutingPoints: ElementAndRoutingPoints[];
}
export interface ElementAndRoutingPoints {
    /**
     * The identifier of an element.
     */
    elementId: string;

    /**
     * The new list of routing points.
     */
    newRoutingPoints?: Point[];
}

export interface ChangeAwareRoutingPointsOperation extends ChangeRoutingPointsOperation {
    kind: typeof ChangeRoutingPointsOperation.KIND;

    /**
     * The routing points of the edge that will be added (may be empty).
     */
    routingPointChanges: ChangeAwareElementAndRoutingPoints[];
}

export namespace ChangeAwareRoutingPointsOperation {
    export function is(object: any): object is ChangeAwareRoutingPointsOperation {
        return Operation.hasKind(object, ChangeRoutingPointsOperation.KIND) && hasArrayProp(object, 'routingPointChanges');
    }

    export function create(routingPointChanges: ChangeAwareElementAndRoutingPoints[]): ChangeAwareRoutingPointsOperation {
        return {
            kind: ChangeRoutingPointsOperation.KIND,
            isOperation: true,
            newRoutingPoints: [],
            routingPointChanges
        };
    }
}

/**
 * The `ElementAndRoutingPoints` type is used to associate an edge with specific routing points.
 */
export interface ChangeAwareElementAndRoutingPoints {
    /*
     * The identifier of an element.
     */
    elementId: string;

    /**
     * The list of new routing points and it's indices.
     */
    newRoutingPoints: [Point, number][];

    /**
     * The list of removed routing points.
     */
    removedRoutingPoints: [Point, number][];
}

export namespace ChangeAwareElementAndRoutingPoints {
    export function is(object: any): object is ChangeAwareElementAndRoutingPoints {
        return (
            hasStringProp(object, 'elementId') && hasArrayProp(object, 'newRoutingPoints') && hasArrayProp(object, 'removedRoutingPoints')
        );
    }
}
