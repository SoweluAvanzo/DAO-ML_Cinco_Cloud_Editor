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
import { Container, GraphModel, GraphModelIndex, Node } from '@cinco-glsp/cinco-glsp-api';
import { ModelElementContainer, getNodeSpecOf, getNodeTypes, NodeType, CreateNodeArgument, HookTypes } from '@cinco-glsp/cinco-glsp-common';
import { CreateNodeOperation, Point } from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';
import { AbstractSpecifiedNodeElementHandler } from './specified_element_handler';
import { HookManager } from '../tools/hook-manager';

@injectable()
export class SpecifiedNodeHandler extends AbstractSpecifiedNodeElementHandler {
    @inject(HookManager)
    protected hookManager: HookManager;

    override executeOperation(operation: CreateNodeOperation): void {
        // find container
        const container: Container | GraphModel | undefined = this.findContainer(operation);

        // CAN
        const parameters: CreateNodeArgument = {
            kind: 'Create',
            modelElementId: '<NONE>',
            elementKind: 'Node',
            containerElementId: container ? container.id : '<NONE>',
            elementTypeId: operation.elementTypeId,
            location: operation.location,
            operation: operation
        };
        const inConstraint = this.canBeContained(container, operation.elementTypeId);
        const canCreate = (): boolean => this.hookManager.executeHook(parameters, HookTypes.CAN_CREATE);
        if (inConstraint || canCreate()) {
            // PRE
            this.hookManager.executeHook(parameters, HookTypes.PRE_CREATE);
            // creation
            const elementTypeId = operation.elementTypeId;
            const relativeLocation = this.getRelativeLocation(operation) ?? Point.ORIGIN;
            const node = this.createNode(relativeLocation, elementTypeId);
            node.index = container!.index;
            container!.containments.push(node);
            // POST
            parameters.modelElementId = node.id;
            parameters.modelElement = node;
            this.hookManager.executeHook(parameters, HookTypes.POST_CREATE);
            this.saveAndUpdate();
        }
    }

    protected createNode(position: Point, elementTypeId: string): Node {
        const specification = getNodeSpecOf(elementTypeId);
        let node;
        if (ModelElementContainer.is(specification)) {
            node = new Container();
            node._containments = [];
        } else {
            node = new Node();
        }
        node.type = elementTypeId;
        node.size = {
            width: node.size.width ?? specification?.width ?? 100,
            height: node.size.height ?? specification?.height ?? 100
        };
        node.position = position;
        node.initializeProperties();
        return node;
    }

    canBeContained(container: Container | GraphModel | undefined, nodeTypeId: string): boolean {
        const specification = getNodeSpecOf(nodeTypeId);
        if (!specification || !container) {
            return false;
        }
        return container.canContain(specification.elementTypeId);
    }

    findContainer(operation: CreateNodeOperation): Container | GraphModel | undefined {
        const index = this.modelState.index as GraphModelIndex;
        return index.findElement(operation.containerId) as Container | GraphModel | undefined;
    }

    override get elementTypeIds(): string[] {
        const result = getNodeTypes()
            .map((e: NodeType) => e.elementTypeId)
            .filter((e: string) => this.BLACK_LIST.indexOf(e) < 0);
        return result;
    }
}
