"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ActivityHooks = void 0;
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
const cinco_glsp_api_1 = require("@cinco-glsp/cinco-glsp-api");
const cinco_glsp_common_1 = require("@cinco-glsp/cinco-glsp-common");
class ActivityHooks extends cinco_glsp_api_1.AbstractNodeHooks {
    constructor() {
        super(...arguments);
        this.CHANNEL_NAME = 'ActivityHooks [' + this.modelState.root.id + ']';
    }
    /**
     * Create
     */
    canCreate(operation) {
        this.log("Triggered canCreate. Can create node of type: " + operation.elementTypeId);
        return true;
    }
    preCreate(container, location) {
        this.log("Triggered preCreate. Creating node in container (" + container.id + ") at position (" + location + ")");
    }
    postCreate(node) {
        this.log("Triggered postCreate on node (" + node.id + ")");
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
    /**
     * Delete
     */
    canDelete(node) {
        this.log("Triggered canDelete on node (" + node.id + ")");
        return true;
    }
    preDelete(node) {
        this.log("Triggered preDelete on node (" + node.id + ")");
        return true;
    }
    postDelete(node) {
        this.log("Triggered postDelete on node (" + node.id + ")");
        return true;
    }
    /**
     * Change Attribute
     */
    canChangeAttribute(node, operation) {
        this.log("Triggered canChangeAttribute on node (" + node.id + ")");
        return operation.change.kind === 'assignValue';
    }
    preAttributeChange(node, operation) {
        this.log("Triggered preAttributeChange on node (" + node.id + ")");
        this.log('Changing: ' + operation.name
            + ' from: ' + node.getProperty(operation.name)
            + " to: " +
            (cinco_glsp_common_1.AssignValue.is(operation.change) ? operation.change.value : 'undefined'));
    }
    postAttributeChange(node, attributeName, oldValue) {
        this.log("Triggered postAttributeChange on node (" + node.id + ")");
        this.log('Changed: ' + attributeName + ' from: ' + oldValue + " to: " + node.getProperty(attributeName));
    }
    /**
     * The following has currently issues
     */
    /**
     * Move
     */
    canMove(node, newPosition) {
        this.log("Triggered canMove on node (" + node.id + ")");
        this.log('Can move to position?: ' + newPosition);
        this.log('CurrentPosition: ' + node.position);
        return true;
    }
    preMove(node, newPosition) {
        this.log("Triggered preMove on node (" + node.id + ")");
        this.log('Moving from: ' + node.position);
        this.log('Moving to: ' + newPosition);
    }
    postMove(node, oldPosition) {
        this.log("Triggered postMove on node (" + node.id + ")");
        this.log('Moved from: ' + oldPosition);
        this.log('Moved to: ' + node.position);
    }
    /**
     * Resize
     */
    canResize(node, newSize) {
        this.log("Triggered canResize on node (" + node.id + ")");
        this.log('can Resize from?: ' + node.size);
        this.log('CurrentSize: ' + newSize);
        return true;
    }
    preResize(node, newSize) {
        this.log("Triggered preResize on node (" + node.id + ")");
        this.log('Resizing from: ' + node.size);
        this.log('Resizing to: ' + newSize);
    }
    postResize(node, oldSize) {
        this.log("Triggered postResize on node (" + node.id + ")");
        this.log('Resized from: ' + oldSize);
        this.log('Resized to: ' + node.size);
    }
    /**
     * The following are not yet implemented
     */
    /**
     * Select
     */
    canSelect(node) {
        this.log("Triggered canSelect on node (" + node.id + ")");
        return true;
    }
    postSelect(node) {
        this.log("Triggered postSelect on node (" + node.id + ")");
        return true;
    }
    /**
     * Double Click
     */
    canDoubleClick(node) {
        this.log("Triggered canDoubleClick on node (" + node.id + ")");
        return true;
    }
    postDoubleClick(node) {
        this.log("Triggered postDoubleClick on node (" + node.id + ")");
    }
}
exports.ActivityHooks = ActivityHooks;
cinco_glsp_api_1.LanguageFilesRegistry.register(ActivityHooks);
