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
import { injectable } from 'inversify';
import { Action, LabeledAction, SetContextActions } from '@eclipse-glsp/sprotty';
import { ServerContextMenuItemProvider } from '@eclipse-glsp/client';

export namespace ServerContextMenu {
    export const CONTEXT_ID = 'context-menu';
}

@injectable()
export class CincoServerContextMenuItemProvider extends ServerContextMenuItemProvider {
    override getContextActionsFromResponse(action: Action): LabeledAction[] {
        if (SetContextActions.is(action)) {
            const actions = action.actions;
            actions.forEach(sa => {
                if ((sa as any).isDisabled) {
                    // cinco cloud workaround. isEnabled is not propagated from server, as it needs to be a function
                    (sa as any).isEnabled = () => !(sa as any).isDisabled;
                }
            });

            return actions;
        }
        return [];
    }
}
