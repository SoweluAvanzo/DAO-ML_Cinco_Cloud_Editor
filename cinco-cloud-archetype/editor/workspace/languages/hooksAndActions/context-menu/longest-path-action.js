"use strict";
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
Object.defineProperty(exports, "__esModule", { value: true });
exports.LongestPathAction = void 0;
const cinco_glsp_api_1 = require("@cinco-glsp/cinco-glsp-api");
class LongestPathAction extends cinco_glsp_api_1.CustomActionHandler {
    getLongest(node, maxSearchDepth) {
        if (node.type === 'node:end') {
            return 0;
        }
        if (maxSearchDepth === 0) {
            return Number.MAX_VALUE;
        }
        let longestPath = Number.MIN_VALUE;
        const successors = node.successors;
        for (const successor of successors) {
            const currentSuccDistance = this.getLongest(successor, maxSearchDepth - 1);
            if (currentSuccDistance > longestPath && currentSuccDistance >= 0) {
                longestPath = currentSuccDistance;
            }
        }
        return longestPath + 1;
    }
    execute(action, ...args) {
        const selectedElements = action.selectedElementIds
            .map(e => this.modelState.index.findNode(e))
            .filter(e => e !== undefined);
        // execute longest path method
        this.logger.info(action.modelElementId);
        selectedElements.forEach(e => {
            const longestPath = this.getLongest(e, 100);
            // TODO: print longest path
            this.logger.info('Longest Path for ' + e.type + '[' + e.id + ']: ' + longestPath);
        });
        return [];
    }
    canExecute(action, ...args) {
        return action.selectedElementIds.length >= 1;
    }
}
exports.LongestPathAction = LongestPathAction;
cinco_glsp_api_1.LanguageFilesRegistry.register(LongestPathAction);
