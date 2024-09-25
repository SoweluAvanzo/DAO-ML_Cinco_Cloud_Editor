"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ExampleValidator = void 0;
/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud and others.
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
 * Language Designer defined example of a Validator
 */
class ExampleValidator extends cinco_glsp_api_1.ValidationHandler {
    constructor() {
        super(...arguments);
        this.CHANNEL_NAME = 'Validator Flowgraph [' + this.modelState.root.id + ']';
    }
    execute(action, ...args) {
        // next actions
        return [
            cinco_glsp_common_1.ValidationResponseAction.create([
                {
                    name: 'Test message',
                    message: 'This is a test message',
                    status: cinco_glsp_common_1.ValidationStatus.Info
                }
            ], action.requestId)
        ];
    }
    canExecute(action, ...args) {
        const element = this.getElement(action.modelElementId);
        return element !== undefined && element.type === 'graphmodel:flowgraph';
    }
}
exports.ExampleValidator = ExampleValidator;
// register into app
cinco_glsp_api_1.LanguageFilesRegistry.register(ExampleValidator);
