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
import { Node, AbstractNodeHooks, LanguageFilesRegistry } from '@cinco-glsp/cinco-glsp-api';
import { Point, PropertyEditOperation, HookTypes } from '@cinco-glsp/cinco-glsp-common';
import { Dimension } from '@eclipse-glsp/server';

export class ActivityHooks extends AbstractNodeHooks {
    static override typeId = 'node:activity';
    static override hookName = 'ActivityHooks';
    static override hookTypes = [
        HookTypes.POST_CREATE,
        HookTypes.PRE_ATTRIBUTE_CHANGE,
        HookTypes.CAN_CHANGE_ATTRIBUTE,
        HookTypes.POST_ATTRIBUTE_CHANGE,
        HookTypes.CAN_MOVE,
        HookTypes.PRE_MOVE,
        HookTypes.POST_MOVE,
        HookTypes.CAN_RESIZE,
        HookTypes.PRE_RESIZE,
        HookTypes.POST_RESIZE
    ];
    override postCreate(node: Node): void {
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

    override canChangeAttribute(operation: PropertyEditOperation): boolean {
        return operation.change.kind === 'assignValue';
    }

    override preAttributeChange(operation: PropertyEditOperation): void {
        console.log('Changing: ' + operation.name);
    }

    override postAttributeChange(node: Node, attributeName: string, oldValue: any): void {
        if (node) {
            console.log('Changed: ' + attributeName + 'from: ' + oldValue);
        }
    }
    override canMove(node: Node, newPosition?: Point): boolean {
        console.log('Can Move?: ' + node.getProperty('name'));
        return true;
    }

    override preMove(node: Node, newPosition?: Point): void {
        console.log('Moving: ' + node.getProperty('name'));
    }

    override postMove(node: Node, oldPosition?: Point): void {
        console.log('Moved: ' + node.getProperty('name'));
    }

    override canResize(node: Node, newSize?: Dimension): boolean {
        this.logger.info('RESIZE?' + node.getProperty('name'));
        return true;
    }
    override preResize(node: Node, newSize?: Dimension): void {
        this.logger.info('RESIZING:' + node.getProperty('name'));
    }
    override postResize(node: Node, oldSize?: Dimension): void {
        this.logger.info('RESIZING:' + node.getProperty('name'));
    }
}

ActivityHooks.register();
LanguageFilesRegistry.register(ActivityHooks);
