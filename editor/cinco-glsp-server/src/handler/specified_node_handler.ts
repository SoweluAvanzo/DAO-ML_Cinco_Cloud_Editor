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
import { Container, GraphModel, GraphModelIndex, Node, HookManager, PrimeReference } from '@cinco-glsp/cinco-glsp-api';
import { ModelElementContainer, getNodeSpecOf, getNodeTypes, NodeType, CreateNodeArgument, HookType } from '@cinco-glsp/cinco-glsp-common';
import { Args, CreateNodeOperation, Point } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { AbstractSpecifiedNodeElementHandler } from './specified_element_handler';

@injectable()
export class SpecifiedNodeHandler extends AbstractSpecifiedNodeElementHandler {
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
            position: operation.location
        };
        const inConstraint = this.canBeContained(container, operation.elementTypeId);
        const canCreate = (): boolean => HookManager.executeHook(parameters, HookType.CAN_CREATE, this.getBundle());
        if (inConstraint && canCreate()) {
            // PRE
            HookManager.executeHook(parameters, HookType.PRE_CREATE, this.getBundle());
            // creation
            const elementTypeId = operation.elementTypeId;
            const relativeLocation = this.getRelativeLocation(operation) ?? Point.ORIGIN;
            const node = this.createNode(relativeLocation, elementTypeId, operation.args);
            node.index = container!.index;
            container!.containments.push(node);
            this.modelState.refresh();
            // POST
            parameters.modelElementId = node.id;
            HookManager.executeHook(parameters, HookType.POST_CREATE, this.getBundle());

            // handle Value Change
            this.handleStateChange(node);
        }
    }

    protected createNode(position: Point, elementTypeId: string, args?: Args): Node {
        const specification = getNodeSpecOf(elementTypeId);
        let node;
        if (ModelElementContainer.is(specification)) {
            node = new Container();
            node.containments = [];
        } else {
            node = new Node();
        }
        node.type = elementTypeId;
        node.size = {
            width: node.size.width ?? specification?.width ?? 100,
            height: node.size.height ?? specification?.height ?? 100
        };
        node.position = position;
        node.initializeProperties(PrimeReference.is(args) ? args : undefined);
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
