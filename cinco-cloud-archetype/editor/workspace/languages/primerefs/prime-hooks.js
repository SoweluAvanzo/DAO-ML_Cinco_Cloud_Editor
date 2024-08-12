"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.PrimeHooks = void 0;
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
const cinco_glsp_api_1 = require("@cinco-glsp/cinco-glsp-api");
class PrimeHooks extends cinco_glsp_api_1.AbstractNodeHook {
    constructor() {
        super(...arguments);
        this.CHANNEL_NAME = 'PrimeHooks [' + this.modelState.root.id + ']';
    }
    /**
     * Double Click
     */
    canDoubleClick(node) {
        if (node.isPrime) {
            this.log('Triggered node is a primeNode!');
        }
        else {
            this.log('Triggered node is not a primeNode!');
        }
        return true;
    }
    postDoubleClick(node) {
        if (node.isPrime) {
            const reference = node.primeReference;
            this.log('Triggered node:\n' + JSON.stringify(reference));
            this.log('Beware! The filePath is a last known location, but the value is unsafe!');
            const referencedModel = this.readModelFromFile(reference.filePath);
            this.log('Read Referenced Model:\nId = ' + (referencedModel === null || referencedModel === void 0 ? void 0 : referencedModel.id));
        }
        else {
            this.log('Triggered node is not a primeNode!');
        }
    }
}
exports.PrimeHooks = PrimeHooks;
cinco_glsp_api_1.LanguageFilesRegistry.register(PrimeHooks);
