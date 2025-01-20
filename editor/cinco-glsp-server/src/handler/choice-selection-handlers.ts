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
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';
import { ChoiceSelectionEdgeSourceOperation, ChoiceSelectionEdgeTargetOperation } from '@cinco-glsp/cinco-glsp-common';

@injectable()
export class ChoiceSelectionEdgeSourceHandler extends CincoJsonOperationHandler {
    readonly operationType = ChoiceSelectionEdgeSourceOperation.KIND;

    override executeOperation({ edgeId, sourceId }: ChoiceSelectionEdgeSourceOperation): void {
        const edge = this.modelState.index.findEdge(edgeId);
        if (edge === undefined) {
            throw new Error(`Undefined edge ${edgeId} while applying sourceID choice selection.`);
        }
        edge.sourceID = sourceId;
        this.saveAndUpdate();
    }
}

@injectable()
export class ChoiceSelectionEdgeTargetHandler extends CincoJsonOperationHandler {
    readonly operationType = ChoiceSelectionEdgeTargetOperation.KIND;

    override executeOperation({ edgeId, targetId }: ChoiceSelectionEdgeTargetOperation): void {
        const edge = this.modelState.index.findEdge(edgeId);
        if (edge === undefined) {
            throw new Error(`Undefined edge ${edgeId} while applying targetID choice selection.`);
        }
        edge.targetID = targetId;
        this.saveAndUpdate();
    }
}
