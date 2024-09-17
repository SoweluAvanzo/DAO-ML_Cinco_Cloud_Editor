/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
import { Node, AbstractNodeHook, LanguageFilesRegistry, Container, GraphModel, ResizeBounds } from '@cinco-glsp/cinco-glsp-api';
import { PropertyEditOperation, AssignValue } from '@cinco-glsp/cinco-glsp-common';
import { Dimension, Point } from '@eclipse-glsp/server';

export class ActivityHook extends AbstractNodeHook {
    override CHANNEL_NAME: string | undefined = 'ActivityHook [' + this.modelState.root.id + ']';

    /**
     * Create
     */

    canCreate(elementTypeId: string, container: Container | GraphModel, location?: Point): boolean {
        this.log('Triggered canCreate. Can create node of type (' + elementTypeId + ') in container (' + container.id + ') at position (' + location + ')');
        return true;
    }

    preCreate(elementTypeId: string, container: Container | GraphModel, location?: Point): void {
        this.log('Triggered preCreate. Creating node of type (' + elementTypeId + ') in container (' + container.id + ') at position (' + location + ')');
    }

    override postCreate(node: Node): void {
        this.log('Triggered postCreate on node (' + node.id + ')');
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
        this.notify("This is a test message in the 'postCreate' hook of ActivityHooks.");
    }

    /**
     * Delete
     */

    override canDelete(node: Node): boolean {
        this.log('Triggered canDelete on node (' + node.id + ')');
        return true;
    }

    override preDelete(node: Node): boolean {
        this.log('Triggered preDelete on node (' + node.id + ')');
        return true;
    }

    override postDelete(node: Node): boolean {
        this.log('Triggered postDelete on node (' + node.id + ')');
        return true;
    }

    /**
     * Change Attribute
     */

    override canAttributeChange(node: Node, operation: PropertyEditOperation): boolean {
        this.log('Triggered canAttributeChange on node (' + node.id + ')');
        return operation.change.kind === 'assignValue';
    }

    override preAttributeChange(node: Node, operation: PropertyEditOperation): void {
        this.log('Triggered preAttributeChange on node (' + node.id + ')');
        this.log(
            'Changing: ' +
                operation.name +
                ' from: ' +
                node.getProperty(operation.name) +
                ' to: ' +
                (AssignValue.is(operation.change) ? operation.change.value : 'undefined')
        );
    }

    override postAttributeChange(node: Node, attributeName: string, oldValue: any): void {
        this.log('Triggered postAttributeChange on node (' + node.id + ')');
        this.log('Changed: ' + attributeName + ' from: ' + oldValue + ' to: ' + node.getProperty(attributeName));
    }

    /**
     * Select
     */

    override canSelect(node: Node, isSelected: boolean): boolean {
        this.log('Triggered canSelect on node (' + node.id + ') - selected: ' + isSelected);
        return true;
    }

    override postSelect(node: Node, isSelected: boolean): boolean {
        this.log('Triggered postSelect on node (' + node.id + ') - selected: ' + isSelected);
        return true;
    }

    /**
     * Double Click
     */

    override canDoubleClick(node: Node): boolean {
        this.log('Triggered canDoubleClick on node (' + node.id + ')');
        return true;
    }

    override postDoubleClick(node: Node): void {
        this.log('Triggered postDoubleClick on node (' + node.id + ')');
    }

    /**
     * Move
     */

    override canMove(node: Node, newPosition: Point): boolean {
        this.log('Triggered canMove on node (' + node.id + ')');
        this.log('Can move to position?: ' + newPosition);
        this.log('CurrentPosition: ' + node.position);
        return true;
    }

    override preMove(node: Node, newPosition: Point): void {
        this.log('Triggered preMove on node (' + node.id + ')');
        this.log('Moving from: ' + node.position);
        this.log('Moving to: ' + newPosition);
    }

    override postMove(node: Node, oldPosition?: Point): void {
        this.log('Triggered postMove on node (' + node.id + ')');
        this.log('Moved from: ' + oldPosition);
        this.log('Moved to: ' + node.position);
    }

    /**
     * Resize
     */

    override canResize(node: Node, resizeBounds: ResizeBounds): boolean {
        this.log('Triggered canResize on node (' + node.id + ')');
        this.log('can Resize from size?: ' + node.size);
        this.log('can Resize from position?: ' + node.position);
        this.log('New Size: ' + resizeBounds.newSize);
        this.log('New Position: ' + resizeBounds.newPosition);
        return true;
    }

    override preResize(node: Node, resizeBounds: ResizeBounds): void {
        this.log('Triggered preResize on node (' + node.id + ')');
        this.log('Resizing from: ' + node.size);
        this.log('Resizing from position: ' + node.position);
        this.log('Resizing to: ' + resizeBounds.newSize);
        this.log('Resizing to position: ' + resizeBounds.newPosition);
    }

    override postResize(node: Node, resizeBounds: ResizeBounds): void {
        this.log('Triggered postResize on node (' + node.id + ')');
        this.log('Resized from: ' + resizeBounds.oldSize);
        this.log('Resized from position: ' + resizeBounds.oldPosition);
        this.log('Resized to: ' + node.size);
        this.log('Resized to position: ' + node.position);
    }
}

LanguageFilesRegistry.register(ActivityHook);
