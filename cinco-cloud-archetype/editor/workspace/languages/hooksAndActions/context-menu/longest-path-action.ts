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

import { CustomActionHandler, LanguageFilesRegistry, Node } from '@cinco-glsp/cinco-glsp-api';
import { Action, CustomAction } from '@cinco-glsp/cinco-glsp-common';

export class LongestPathAction extends CustomActionHandler {
    getLongest(node: Node, maxSearchDepth: number): number {
        if (node.type === 'hooksandactions:end') {
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

    override execute(action: CustomAction, ...args: any): Promise<Action[]> | Action[] {
        const selectedElements: Node[] = action.selectedElementIds
            .map(e => this.modelState.index.findNode(e))
            .filter(e => e !== undefined) as Node[];
        // execute longest path method
        this.logger.info(action.modelElementId);
        selectedElements.forEach(e => {
            const longestPath = this.getLongest(e, 100);
            this.logger.info('Longest Path for ' + e.type + '[' + e.id + ']: ' + longestPath);
        });

        return [];
    }

    override canExecute(action: CustomAction, ...args: unknown[]): boolean | Promise<boolean> {
        return action.selectedElementIds.length >= 1;
    }
}

LanguageFilesRegistry.register(LongestPathAction);
