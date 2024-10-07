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

import { Action, DeleteElementOperation, GModelElement, MouseListener, MouseTool, Tool } from '@eclipse-glsp/client';
import { inject, injectable } from 'inversify';
import { CincoButtonDelete, CincoButtonRestore } from '../../model/model';
import { RestoreModelElementOperation } from '@cinco-glsp/cinco-glsp-common';

@injectable()
export class GhostDecisionTool implements Tool {
    static readonly ID = 'ghost-decision-tool';

    @inject(MouseTool)
    protected mouseTool: MouseTool;
    protected mouseListener: MouseListener;

    get id(): string {
        return GhostDecisionTool.ID;
    }

    enable(): void {
        this.mouseListener = new GhostDecisionMouseListener();
        this.mouseTool.register(this.mouseListener);
    }

    disable(): void {
        this.mouseTool.deregister(this.mouseListener);
    }
}

export class GhostDecisionMouseListener extends MouseListener {
    override mouseDown(target: GModelElement, _event: MouseEvent): (Action | Promise<Action>)[] {
        if (target instanceof CincoButtonDelete) {
            return [DeleteElementOperation.create([target.modelElementID])];
        }
        if (target instanceof CincoButtonRestore) {
            return [RestoreModelElementOperation.create(target.modelElementID)];
        }
        return [];
    }
}
