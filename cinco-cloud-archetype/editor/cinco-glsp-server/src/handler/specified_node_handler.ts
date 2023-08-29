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
import { ModelElementContainer, getNodeSpecOf, getNodeTypes , NodeType } from '@cinco-glsp/cinco-glsp-common';
import {
    ActionDispatcher,
    CreateNodeOperation,
    Point,
    SaveModelAction
} from '@eclipse-glsp/server-node';
import { inject, injectable } from 'inversify';
import { SpecifiedElementHandler } from './specified_element_handler';

@injectable()
export class SpecifiedNodeHandler extends SpecifiedElementHandler {
    @inject(GraphModelIndex)
    protected index: GraphModelIndex;
    @inject(ActionDispatcher)
    readonly actionDispatcher: ActionDispatcher;

    override BLACK_LIST: string[] = [];

    override get operationType(): string {
        return 'createNode';
    }

    override execute(operation: CreateNodeOperation): void {
        // find container
        const container: Container | GraphModel | undefined = this.getValidConstrainedContainer(operation);
        if (container === undefined) {
            return;
        }
        const paletteUpdateAction = {
            kind: 'enableToolPalette'
        };
        // pre hook
        this.preCreateHook(operation.elementTypeId, container.id, operation.location);
        // creation
        const elementTypeId = operation.elementTypeId;
        const relativeLocation = this.getRelativeLocation(operation) ?? Point.ORIGIN;
        const node = this.createNode(relativeLocation, elementTypeId);
        node.index = container.index;
        container.containments.push(node);
        // post hook
        this.postCreateHook(node);
        // save model
        const graphmodel = this.index.getRoot();
        const fileUri = graphmodel._sourceUri;
        this.actionDispatcher.dispatch(SaveModelAction.create({ fileUri }));
        // update palette
        this.actionDispatcher.dispatch(paletteUpdateAction);
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
            width: specification?.width ?? 0,
            height: specification?.height ?? 0
        };
        node.position = position;
        node.initializeProperties();
        return node;
    }

    getValidConstrainedContainer(operation: CreateNodeOperation): Container | GraphModel | undefined {
        // find container
        const container: Container | GraphModel | undefined = this.findContainer(operation);
        if (container === undefined) {
            return undefined;
        }
        // check constraint
        const specification = getNodeSpecOf(operation.elementTypeId);
        if (!specification) {
            return undefined;
        }
        const canBeContained = container.canContain(specification.elementTypeId);
        // if constraint is met, return container
        return canBeContained ? container : undefined;
    }

    findContainer(operation: CreateNodeOperation): Container | GraphModel | undefined {
        const index = this.index as GraphModelIndex;
        return index.findElement(operation.containerId) as Container | GraphModel | undefined;
    }

    override get elementTypeIds(): string[] {
        const result = getNodeTypes()
            .map((e: NodeType) => e.elementTypeId)
            .filter((e: string) => this.BLACK_LIST.indexOf(e) < 0);
        return result;
    }

    protected preCreateHook(elementTypeId: string, containerId: string, location: Point | undefined): void {
        // TODO: preCreate
        return;
    }

    protected postCreateHook(node: Node): void {
        // TODO: generalize postCreate
        if (node.type !== 'node:activity') {
            return;
        }
        const activityNames = [
            'Close',
            'Fix',
            'Give',
            'Look at',
            'Open',
            'Pick up',
            'Pull',
            'Push',
            'Put on',
            'Read',
            'Take off',
            'Talk to',
            'Turn off',
            'Turn on',
            'Unlock',
            'Use',
            'Walk to'
        ];
        const randomActivityName = activityNames[Math.trunc((Math.random() * 10000) % activityNames.length)];
        node.setProperty('name', randomActivityName);
    }
}
