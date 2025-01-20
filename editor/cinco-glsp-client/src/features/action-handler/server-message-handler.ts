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
import { ServerDialogAction, ServerOutputAction, CommandAction } from '@cinco-glsp/cinco-glsp-common';
import { Action, IActionDispatcher, IActionHandler, ILogger, TYPES } from '@eclipse-glsp/client';
import { inject, injectable } from 'inversify';
import { EnvironmentProvider, IEnvironmentProvider } from '../../api/environment-provider';

@injectable()
export class ServerMessageHandler implements IActionHandler {
    @inject(TYPES.IActionDispatcher)
    protected dispatcher: IActionDispatcher;
    @inject(TYPES.ILogger)
    protected logger: ILogger;
    @inject(EnvironmentProvider)
    protected environmentProvider: IEnvironmentProvider;

    handle(action: Action): void {
        if (ServerOutputAction.is(action)) {
            this.environmentProvider.handleLogging(action);
        } else if (ServerDialogAction.is(action)) {
            this.environmentProvider.showDialog(action);
        } else if (CommandAction.is(action)) {
            this.environmentProvider.handleCommand(action);
        }
    }
}
