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
import { Action, ActionHandler, Logger, MaybePromise } from '@eclipse-glsp/server-node';

import { inject, injectable } from 'inversify';
import { CustomAction } from '../context-menu/custom-context-menu-item-provider';
import { Node } from '../model/graph-model';
import { GraphModelIndex } from '../model/graph-model-index';

@injectable()
export class ShortestPathAction implements ActionHandler {
    @inject(Logger)
    readonly logger: Logger;

    @inject(GraphModelIndex)
    readonly index: GraphModelIndex;

    actionKinds: string[] = ['ExampleAction'];

    execute(action: CustomAction, ...args: unknown[]): MaybePromise<Action[]> {
        const selectedElementIds: string[] = action.selectedElementIds;

        // ids to nodes
        const selectedElements: Node[] = selectedElementIds.map(e => this.index.findNode(e)).filter(e => e !== undefined) as Node[];

        // execute shortest path method
        selectedElements.forEach(e => {
            const shortestPath = this.getShortest(e, 100);
            // TODO: print shortest path
            this.logger.info('Shortest Path for ' + e.type + '[' + e.id + ']: ' + shortestPath);
        });

        // next actions
        const consecutiveActions: Action[] = [];
        return consecutiveActions;
    }

    getShortest(node: Node, maxSearchDepth: number): number {
        if (node.type === 'node:end') {
            return 0;
        }
        if (maxSearchDepth === 0) {
            return Number.MIN_VALUE;
        }

        let shortestPath = Number.MAX_VALUE;
        const successors = node.successors;
        for (const successor of successors) {
            const currentSuccDistance = this.getShortest(successor, maxSearchDepth - 1);
            if (currentSuccDistance < shortestPath && currentSuccDistance >= 0) {
                shortestPath = currentSuccDistance;
            }
        }
        return shortestPath + 1;
    }

    priority?: number | undefined;
}
