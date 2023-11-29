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
import { GraphModelState } from '@cinco-glsp/cinco-glsp-api';
import { Action, ActionHandler, MaybePromise, SaveModelAction } from '@eclipse-glsp/server/node';
import { inject, injectable } from 'inversify';

@injectable()
export class CompoundHandler implements ActionHandler {
    actionKinds: string[] = ['compound'];

    @inject(GraphModelState)
    readonly modelState: GraphModelState;

    execute(action: Action, ...args: unknown[]): MaybePromise<Action[]> {
        const fileUri = this.modelState.graphModel._sourceUri;
        return [SaveModelAction.create({ fileUri })];
    }
}
