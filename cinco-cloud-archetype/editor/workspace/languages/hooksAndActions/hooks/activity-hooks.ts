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
import { Node, AbstractNodeHook, LanguageFilesRegistry, Container } from '@cinco-glsp/cinco-glsp-api';
import { PropertyEditOperation, AssignValue } from '@cinco-glsp/cinco-glsp-common';
import { CreateNodeOperation, Dimension, Point } from '@eclipse-glsp/server';

export class ActivityHook extends AbstractNodeHook {
    override CHANNEL_NAME: string | undefined = 'ActivityHook [' + this.modelState.root.id + ']';

    /**
     * Create
     */

    override canCreate(operation: CreateNodeOperation): boolean {
        this.log('Triggered canCreate. Can create node of type: ' + operation.elementTypeId);
        return true;
    }

    override preCreate(container: Container, location: Point | undefined): void {
        this.log('Triggered preCreate. Creating node in container (' + container.id + ') at position (' + location + ')');
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

    override canChangeAttribute(node: Node, operation: PropertyEditOperation): boolean {
        this.log('Triggered canChangeAttribute on node (' + node.id + ')');
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
     * The following has currently issues
     */

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

    override canResize(node: Node, newSize: Dimension): boolean {
        this.log('Triggered canResize on node (' + node.id + ')');
        this.log('can Resize from?: ' + node.size);
        this.log('CurrentSize: ' + newSize);
        return true;
    }

    override preResize(node: Node, newSize: Dimension): void {
        this.log('Triggered preResize on node (' + node.id + ')');
        this.log('Resizing from: ' + node.size);
        this.log('Resizing to: ' + newSize);
    }

    override postResize(node: Node, oldSize: Dimension): void {
        this.log('Triggered postResize on node (' + node.id + ')');
        this.log('Resized from: ' + oldSize);
        this.log('Resized to: ' + node.size);
    }

    /**
     * The following are not yet implemented
     */

    /**
     * Select
     */

    override canSelect(node: Node): boolean {
        this.log('Triggered canSelect on node (' + node.id + ')');
        return true;
    }

    override postSelect(node: Node): boolean {
        this.log('Triggered postSelect on node (' + node.id + ')');
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
}

LanguageFilesRegistry.register(ActivityHook);
