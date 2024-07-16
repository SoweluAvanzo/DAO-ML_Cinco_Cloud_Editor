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
import {
    CreateEdgeOperation,
    CreateOperation,
    Dimension,
    ReconnectEdgeOperation,
    CreateNodeOperation,
    DeleteElementOperation
} from '@eclipse-glsp/server';
import { ManagedBaseAction, Operation } from '../shared-protocol';
import { PropertyEditOperation } from '../property-protocol';
import { Point } from '../../meta-specification';

export type OperationArgument = Argument &
    (
        | AttributeChangeArgument
        | MoveArgument
        | ResizeArgument
        | ReconnectArgument
        | DeleteArgument
        | CreateArgument
        | SelectArgument
        | DoubleClickArgument
    );

interface Argument extends ManagedBaseAction {
    modelElementId: string;
    operation?: Operation;
    kind:
        | 'CreateNode'
        | 'CreateEdge'
        | 'CreateGraphModel'
        | 'Delete'
        | 'AttributeChange'
        | 'Move'
        | 'Reconnect'
        | 'Resize'
        | 'Create'
        | 'Select'
        | 'DoubleClick';
}

export interface AttributeChangeArgument extends Argument {
    kind: 'AttributeChange';
    operation: PropertyEditOperation;
    oldValue: any;
}
export type CreateArgument = CreateEdgeArgument | CreateNodeArgument | CreateGraphModelArgument | CreateUserDefinedTypeArgument;
interface CreateArgumentInterface extends Argument {
    kind: 'Create';
    elementKind: 'Node' | 'Edge' | 'GraphModel' | 'UserDefinedType'; // TODO:
    elementTypeId: string;
    operation: CreateOperation;
}

export interface CreateNodeArgument extends CreateArgumentInterface {
    elementKind: 'Node';
    containerElementId: string;
    location?: Point;
    operation: CreateNodeOperation;
}

export interface CreateEdgeArgument extends CreateArgumentInterface {
    elementKind: 'Edge';
    operation: CreateEdgeOperation;
    sourceElementId: string;
    targetElementId: string;
}

export interface CreateGraphModelArgument extends CreateArgumentInterface {
    elementKind: 'GraphModel';
    path: string;
}

export interface CreateUserDefinedTypeArgument extends CreateArgumentInterface {
    elementKind: 'UserDefinedType';
    args: any;
}

export interface DeleteArgument extends Argument {
    kind: 'Delete';
    operation: DeleteElementOperation;
    deleted: any;
}

export interface MoveArgument extends Argument {
    newPosition?: Point;
    oldPosition: Point;
    kind: 'Move';
}
export interface ReconnectArgument extends Argument {
    sourceId: string;
    targetId: string;
    operation: ReconnectEdgeOperation;
    kind: 'Reconnect';
}

export interface ResizeArgument extends Argument {
    newSize: Dimension;
    oldSize: Dimension;
    newPosition: Point;
    oldPosition: Point;
    kind: 'Resize';
}

export interface SelectArgument extends Argument {
    kind: 'Select';
    selectedElements: string[];
    deselectedElements: string[];
    isSelected: boolean;
}

export interface DoubleClickArgument extends Argument {
    kind: 'DoubleClick';
}
