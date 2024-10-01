/********************************************************************************
 * Copyright (c) 2020-2022 Cinco Cloud and others.
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
import { MetaSpecification, MetaSpecificationResponseAction } from '@cinco-glsp/cinco-glsp-common';
import { Action, IActionHandler, ICommand } from '@eclipse-glsp/client';
import { injectable, inject } from 'inversify';
import { EnvironmentProvider, IEnvironmentProvider } from '../api/environment-provider';

@injectable()
export class MetaSpecificationResponseHandler implements IActionHandler {
    @inject(EnvironmentProvider) environmentProvider: IEnvironmentProvider;
    protected static _registration_callbacks: Map<string, (() => void)[]> = new Map();

    static addRegistrationCallback(clientId: string, registrationCallback: () => void): void {
        if (!this._registration_callbacks.has(clientId)) {
            this._registration_callbacks.set(clientId, []);
        }
        this._registration_callbacks.get(clientId)!.push(registrationCallback);
    }

    static removeRegistrationCallback(clientId: string): boolean {
        return this._registration_callbacks.delete(clientId);
    }

    handle(action: MetaSpecificationResponseAction): void | Action | ICommand {
        MetaSpecificationResponseHandler.handleResponse(action, this.environmentProvider);
    }

    static handleResponse(action: MetaSpecificationResponseAction, environmentProvider: IEnvironmentProvider): void {
        const metaSpec = action.metaSpecification;
        MetaSpecification.clear();
        MetaSpecification.merge(metaSpec);
        MetaSpecification.prepareCache();
        if (MetaSpecificationResponseHandler._registration_callbacks) {
            for (const client of this._registration_callbacks.keys()) {
                for (const cb of this._registration_callbacks.get(client)!) {
                    try {
                        cb();
                    } catch (e) {
                        console.log(e);
                    }
                }
            }
        }
        environmentProvider.propagateMetaspecification(MetaSpecification.get());
    }
}
