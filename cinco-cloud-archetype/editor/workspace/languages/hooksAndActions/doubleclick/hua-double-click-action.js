"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.HooksAndActionsExampleDoubleClickHandler = void 0;
/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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
/**
 * Language Designer defined example of a DoubleClickHandler
 */
class HooksAndActionsExampleDoubleClickHandler extends cinco_glsp_api_1.DoubleClickHandler {
    constructor() {
        super(...arguments);
        this.CHANNEL_NAME = 'HUA [' + this.modelState.root.id + ']';
    }
    execute(action, ...args) {
        // parse action
        const modelElementId = action.modelElementId;
        const element = this.modelState.index.findElement(modelElementId);
        // logging
        const message = 'Element [' + element.type + '] was double-clicked with id: ' + element.id;
        this.log(message, { show: true });
        this.dialog('DoubleClickEvent!', message).then(dialogResult => {
            const buttonText = dialogResult === 'true' ? 'OK' : 'Cancel';
            this.notify('You clicked: ' + buttonText);
            this.saveModel();
            this.notify('saved Model');
            this.submitModel();
            this.notify('submitted Model');
        });
        // next actions => find all activities and update their appearance
        const consecutiveActions = [];
        const allGAcitivties = this.modelState.index.getModelElements('hooksandactions:activity');
        const allAcitivties = allGAcitivties.map((e) => this.modelState.index.findElement(e.id));
        allAcitivties.forEach(a => consecutiveActions.push(cinco_glsp_common_1.RequestAppearanceUpdateAction.create(a.id)));
        return consecutiveActions;
    }
    canExecute(action, ...args) {
        const element = this.getElement(action.modelElementId);
        return element !== undefined && element.type === 'hooksandactions:activity';
    }
}
exports.HooksAndActionsExampleDoubleClickHandler = HooksAndActionsExampleDoubleClickHandler;
// register into app
cinco_glsp_api_1.LanguageFilesRegistry.register(HooksAndActionsExampleDoubleClickHandler);
