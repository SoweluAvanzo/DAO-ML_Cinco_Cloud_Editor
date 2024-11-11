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
import { Dimension, ReconnectEdgeOperation } from '@eclipse-glsp/server';
import { ManagedBaseAction, Operation } from '../shared-protocol';
import { PropertyEditOperation } from '../property-protocol';
import { Point } from '../../meta-specification';
import { Cell } from '../../model/cell';

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
        | ModelFileChangeArgument
        | SaveModelFileArgument
        | OpenModelFileArgument
    );

interface Argument extends ManagedBaseAction {
    modelElementId: string;
    elementTypeId?: string;
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
        | 'DoubleClick'
        | 'ModelFileChange'
        | 'SaveModelFile'
        | 'OpenModelFile';
}

export interface AttributeChangeArgument extends Argument {
    kind: 'AttributeChange';
    operation: PropertyEditOperation;
    oldValue: any;
}
export type CreateArgument = CreateEdgeArgument | CreateNodeArgument | CreateGraphModelArgument | CreateUserDefinedTypeArgument;
interface CreateArgumentInterface extends Argument {
    kind: 'Create';
    elementKind: 'Node' | 'Edge' | 'GraphModel' | 'UserDefinedType'; // TODO-SAMI: This is currently not further implemented
    elementTypeId: string;
}

export interface CreateNodeArgument extends CreateArgumentInterface {
    elementKind: 'Node';
    containerElementId: string;
    position?: Point;
}

export interface CreateEdgeArgument extends CreateArgumentInterface {
    elementKind: 'Edge';
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
    deleted: any;
}

export interface MoveArgument extends Argument {
    newPosition?: Point;
    oldPosition: Point;
    kind: 'Move';
}
export interface ReconnectArgument extends Argument {
    sourceId: Cell<string>;
    targetId: Cell<string>;
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

export interface ModelFileChangeArgument extends Argument {
    kind: 'ModelFileChange';
}

export interface SaveModelFileArgument extends Argument {
    kind: 'SaveModelFile';
    path: string;
}

export interface OpenModelFileArgument extends Argument {
    kind: 'OpenModelFile';
}
