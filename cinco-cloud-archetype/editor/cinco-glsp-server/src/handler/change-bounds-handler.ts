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

import { ChangeBoundsOperation, Dimension, GNode, Point } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';
import { ResizeArgument, HookType, MoveArgument } from '@cinco-glsp/cinco-glsp-common';
import { Node } from '@cinco-glsp/cinco-glsp-api/lib/model/graph-model';
import { HookManager } from '@cinco-glsp/cinco-glsp-api';

@injectable()
export class ChangeBoundsHandler extends CincoJsonOperationHandler {
    readonly operationType = ChangeBoundsOperation.KIND;

    override executeOperation(operation: ChangeBoundsOperation): void {
        for (const element of operation.newBounds) {
            this.changeElementBounds(element.elementId, element.newSize, element.newPosition);
        }
    }

    protected changeElementBounds(elementId: string, newSize?: Dimension, newPosition?: Point): void {
        const index = this.modelState.index;
        const node = index.findByClass(elementId, GNode);
        const nodeObj = node ? index.findNode(node.id) : undefined;
        if (nodeObj) {
            const oldSize = nodeObj.size;
            const oldPosition = nodeObj.position;
            const isResize = oldSize && newSize && (oldSize.width !== newSize.width || oldSize.height !== newSize.height);
            const isMove = !isResize && oldPosition && newPosition && !Point.equals(oldPosition, newPosition);
            if (isResize) {
                this.handleResize(nodeObj, newSize, newPosition);
            } else if (isMove) {
                this.handleMove(nodeObj, newPosition);
            }
        }
    }

    private handleResize(node: Node, newSize: Dimension, newPosition: Point | undefined): void {
        const oldSize = node.size;
        const oldPosition = node.position;
        const parameters: ResizeArgument = {
            kind: 'Resize',
            modelElementId: node.id,
            oldSize: oldSize,
            newSize: newSize,
            oldPosition: oldPosition,
            newPosition: newPosition ?? oldPosition
        };
        const canResize = HookManager.executeHook(
            parameters,
            HookType.CAN_RESIZE,
            this.modelState,
            this.logger,
            this.actionDispatcher,
            this.sourceModelStorage,
            this.submissionHandler
        );
        if (canResize) {
            HookManager.executeHook(
                parameters,
                HookType.PRE_RESIZE,
                this.modelState,
                this.logger,
                this.actionDispatcher,
                this.sourceModelStorage,
                this.submissionHandler
            );
            if (newPosition) {
                node.position = newPosition;
            }
            node.size = newSize;
            HookManager.executeHook(
                parameters,
                HookType.POST_RESIZE,
                this.modelState,
                this.logger,
                this.actionDispatcher,
                this.sourceModelStorage,
                this.submissionHandler
            );
        }
    }

    private handleMove(node: Node, newPosition: Point): void {
        const oldPosition = node.position;
        const parameters: MoveArgument = {
            kind: 'Move',
            modelElementId: node.id,
            oldPosition: oldPosition,
            newPosition: newPosition
        };
        const canMove = HookManager.executeHook(
            parameters,
            HookType.CAN_MOVE,
            this.modelState,
            this.logger,
            this.actionDispatcher,
            this.sourceModelStorage,
            this.submissionHandler
        );
        if (canMove) {
            HookManager.executeHook(
                parameters,
                HookType.PRE_MOVE,
                this.modelState,
                this.logger,
                this.actionDispatcher,
                this.sourceModelStorage,
                this.submissionHandler
            );
            node.position = newPosition;
            HookManager.executeHook(
                parameters,
                HookType.POST_MOVE,
                this.modelState,
                this.logger,
                this.actionDispatcher,
                this.sourceModelStorage,
                this.submissionHandler
            );
        }
    }
}
