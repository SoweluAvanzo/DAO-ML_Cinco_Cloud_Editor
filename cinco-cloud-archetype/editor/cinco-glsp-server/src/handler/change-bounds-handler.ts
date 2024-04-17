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
import {ResizeArgument, HookTypes, MoveArgument} from '@cinco-glsp/cinco-glsp-common';
import { HookManager } from '../tools/hook-manager';

@injectable()
export class ChangeBoundsHandler extends CincoJsonOperationHandler {
    readonly operationType = ChangeBoundsOperation.KIND;

    protected hookManager: HookManager;

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
            if (newSize !== nodeObj.size) {
                const parameters: ResizeArgument = {
                    modelElementId: elementId,
                    kind: 'Resize',
                    newSize: newSize,
                    oldSize: nodeObj.size
                };
                if(!this.hookManager){
                    this.hookManager = HookManager.getInstance();
                }
                if (this.canResize(newSize, parameters)) {
                    this.hookManager.executeHook(parameters, HookTypes.PRE_RESIZE);
                    nodeObj.size = newSize;
                    this.hookManager.executeHook(parameters, HookTypes.POST_RESIZE);
                }
            }
            const moveParameters: MoveArgument = {
                kind: 'Move',
                modelElementId: elementId,
                newPosition: newPosition,
                oldPosition: nodeObj.position
            };

            if (newPosition && this.canMove( moveParameters)) {
                this.hookManager.executeHook(moveParameters, HookTypes.PRE_MOVE);
                nodeObj.position = newPosition;
                this.hookManager.executeHook(moveParameters, HookTypes.POST_MOVE);
            }
        }
    }

    private canMove(parameters: MoveArgument): boolean {
        return this.hookManager.executeHook(parameters, HookTypes.CAN_MOVE);
    }

    private canResize(newSize: Dimension, parameters: ResizeArgument): boolean {
        return this.hookManager.executeHook(parameters, HookTypes.CAN_RESIZE);
    }
}

