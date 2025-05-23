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

import { SelectAction as GLSPSelectAction } from '@eclipse-glsp/protocol';
import { ManagedBaseAction } from './shared-protocol';

export interface SelectAction extends ManagedBaseAction, GLSPSelectAction {
    kind: typeof SelectAction.KIND;
    root: string;
}
export namespace SelectAction {
    export const KIND = GLSPSelectAction.KIND;

    export function create(root: string, selectedElements: string[], deselectedElements: string[] = []): SelectAction {
        const action = GLSPSelectAction.create({
            selectedElementsIDs: selectedElements,
            deselectedElementsIDs: deselectedElements
        }) as SelectAction;
        action.root = root;
        action.modelElementId = selectedElements.length > 0 ? selectedElements[0] : deselectedElements[0]; // just a representant
        return action;
    }
}
