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
import { ValidationResponseAction } from '@cinco-glsp/cinco-glsp-common';
import { ICommand, Action, IActionHandler, ILogger, TYPES } from '@eclipse-glsp/client';
import { inject, injectable } from 'inversify';
import { EnvironmentProvider, IEnvironmentProvider } from '../api/environment-provider';

@injectable()
export class CincoValidationResponseHandler implements IActionHandler {
    @inject(TYPES.ILogger)
    protected logger: ILogger;
    @inject(EnvironmentProvider)
    protected environmentProvider: IEnvironmentProvider;

    handle(action: ValidationResponseAction): void | Action | ICommand {
        this.environmentProvider.handleValidation(action);
    }
}
