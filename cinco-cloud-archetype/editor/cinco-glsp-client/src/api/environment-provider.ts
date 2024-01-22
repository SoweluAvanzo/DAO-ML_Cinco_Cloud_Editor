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

import { inject, injectable } from 'inversify';
import { IActionDispatcher, IDiagramStartup, ILogger, TYPES } from '@eclipse-glsp/client';
import {
    CompositionSpecification,
    PropertyViewResponseAction,
    ServerDialogAction,
    ServerOutputAction
} from '@cinco-glsp/cinco-glsp-common';
import { ServerArgsProvider } from '../meta/server-args-response-handler';

export const EnvironmentProvider = Symbol('IEnvironmentProvider');
export interface IEnvironmentProvider extends IDiagramStartup {
    getWorkspaceRoot(): Promise<string>;
    handleLogging(action: ServerOutputAction): void | Promise<void>;
    showDialog(action: ServerDialogAction): void | Promise<void>;
    selectedElementChanged(modelElementId: string): void | Promise<void>;
    provideProperties(action: PropertyViewResponseAction): void | Promise<void>;
    propagateMetaspecification(metaSpec: CompositionSpecification): void | Promise<void>;
}

@injectable()
export class DefaultEnvironmentProvider implements IEnvironmentProvider {
    @inject(TYPES.IActionDispatcher) actionDispatcher: IActionDispatcher;
    @inject(TYPES.ILogger) protected logger: ILogger;

    protected selectedElementId: string;

    async getWorkspaceRoot(): Promise<string> {
        const serverArgs = await ServerArgsProvider.getServerArgs();
        return serverArgs?.workspacePath;
    }

    handleLogging(action: ServerOutputAction): void | Promise<void> {
        switch (
            action.logLevel // "NONE" | "INFO" | "WARNING" | "ERROR" | "FATAL" | "OK"
        ) {
            case 'INFO':
                this.logger.info(this, action.message);
                break;
            case 'ERROR':
                this.logger.error(this, action.message);
                break;
            case 'FATAL':
                this.logger.error(this, action.message);
                break;
            case 'WARNING':
                this.logger.warn(this, action.message);
                break;
            default:
                this.logger.log(this, action.message);
                break;
        }
    }

    showDialog(action: ServerDialogAction): void {
        alert('ShowDialog not implemented: ' + action.message);
    }

    selectedElementChanged(modelElementId: string): void | Promise<void> {
        this.selectedElementId = modelElementId;
    }

    provideProperties(action: PropertyViewResponseAction): void | Promise<void> {
        this.logger.log(this, 'No propaties view available.');
    }

    propagateMetaspecification(metaSpec: CompositionSpecification): void | Promise<void> {
        this.logger.log(this, 'Metaspecification does not need to be propagated.');
    }

    postRequestModel(): void {
        this.logger.log(this, 'Environment Provider loaded.');
    }
}
