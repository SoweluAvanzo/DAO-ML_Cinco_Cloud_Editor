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
import { inject, injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';
import { ResizeArgument, HookTypes, MoveArgument } from '@cinco-glsp/cinco-glsp-common';
import { HookManager } from '../tools/hook-manager';
import { Node } from '@cinco-glsp/cinco-glsp-api/lib/model/graph-model';

@injectable()
export class ChangeBoundsHandler extends CincoJsonOperationHandler {
    @inject(HookManager)
    protected hookManager: HookManager;
    readonly operationType = ChangeBoundsOperation.KIND;

    override executeOperation(operation: ChangeBoundsOperation): void {
        for (const element of operation.newBounds) {
            this.changeElementBounds(element.elementId, element.newSize, element.newPosition);
        }
    }

    protected changeElementBounds(elementId: string, newSize: Dimension, newPosition?: Point): void {
        const index = this.modelState.index;
        const node = index.findByClass(elementId, GNode);
        const nodeObj = node ? index.findNode(node.id) : undefined;
        if (nodeObj) {
            this.handleMove(nodeObj, newPosition);
            this.handleResize(nodeObj, newSize);
        }
    }

    private handleMove(node: Node, newPosition: Point | undefined): void {
        const oldPosition = node.position;
        if (!newPosition || !this.isMove(oldPosition, newPosition)) {
            return;
        }
        const moveParameters: MoveArgument = {
            kind: 'Move',
            modelElementId: node.position,
            oldPosition: node.position,
            newPosition: newPosition
        };
        if (newPosition && this.canMove(moveParameters)) {
            this.hookManager.executeHook(moveParameters, HookTypes.PRE_MOVE);
            node.position = newPosition;
            this.hookManager.executeHook(moveParameters, HookTypes.POST_MOVE);
        }
    }

    private handleResize(node: Node, newSize: Dimension | undefined): void {
        const oldSize = node.size;
        if (!newSize || !oldSize || !this.isResize(oldSize, newSize)) {
            return;
        }
        if (newSize !== node.size) {
            const parameters: ResizeArgument = {
                kind: 'Resize',
                modelElementId: node.id,
                oldSize: node.size,
                newSize: newSize
            };
            if (this.canResize(newSize, parameters)) {
                this.hookManager.executeHook(parameters, HookTypes.PRE_RESIZE);
                node.size = newSize;
                this.hookManager.executeHook(parameters, HookTypes.POST_RESIZE);
            }
        }
    }

    private isMove(oldPosition: Point, newPosition: Point): boolean {
        return true; // TODO-SAMI
    }

    private isResize(oldDim: Dimension, newDim: Dimension): boolean {
        return true; // TODO-SAMI
    }

    private canMove(parameters: MoveArgument): boolean {
        return this.hookManager.executeHook(parameters, HookTypes.CAN_MOVE);
    }

    private canResize(newSize: Dimension, parameters: ResizeArgument): boolean {
        return this.hookManager.executeHook(parameters, HookTypes.CAN_RESIZE);
    }
}
