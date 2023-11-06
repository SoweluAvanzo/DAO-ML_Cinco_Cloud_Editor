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
import { Action, IActionDispatcher, IActionHandler, ICommand, TYPES } from '@eclipse-glsp/client';
import { CommandService } from '@theia/core';
import { injectable, inject, optional } from 'inversify';
import { DynamicImportLoader } from './dynamic-import-tool';

@injectable()
export class MetaSpecificationResponseHandler implements IActionHandler {
    @inject(CommandService)
    @optional()
    private readonly commandService: CommandService;
    @inject(TYPES.IActionDispatcher)
    private readonly actionDispatcher: IActionDispatcher;

    static _unlock: () => void;
    static _meta_spec_loaded = new Promise<void>((resolve, reject) => {
        MetaSpecificationResponseHandler._unlock = resolve;
    });
    static _registration_callbacks: (() => void)[] = [];

    static addRegistrationCallback(registrationCallback: () => void): void {
        this._registration_callbacks.push(registrationCallback);
    }

    handle(action: MetaSpecificationResponseAction): void | Action | ICommand {
        const metaSpec = action.metaSpecification;
        MetaSpecification.clear();
        MetaSpecification.merge(metaSpec);
        MetaSpecificationResponseHandler._unlock();
        if (MetaSpecificationResponseHandler._registration_callbacks) {
            for (const cb of MetaSpecificationResponseHandler._registration_callbacks) {
                try {
                    cb();
                } catch (e) {
                    console.log(e);
                }
            }
        }
        // propagate to theia
        this.propagateToTheia();
        // update css
        DynamicImportLoader.load(this.actionDispatcher);
        // update palette after meta-specification is updated
        return {
            kind: 'enableToolPalette'
        };
    }

    propagateToTheia(): void {
        if (this.commandService) {
            this.commandService.executeCommand('cinco.language_update', {
                metaSpecification: MetaSpecification.get()
            });
        }
    }
}
