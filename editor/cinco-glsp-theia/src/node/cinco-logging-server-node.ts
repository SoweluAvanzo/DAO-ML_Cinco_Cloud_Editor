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
import { injectable } from 'inversify';
import { CincoLoggingClient, CincoLoggingServer } from '../common/cinco-logging-protocol';
import { CincoLogger } from './cinco-theia-logger';
import { inject } from '@theia/core/shared/inversify';

@injectable()
export class CincoLoggingServerNode implements CincoLoggingServer {
    @inject(CincoLogger)
    protected readonly logger: CincoLogger;
    client: CincoLoggingClient | undefined;

    async connect(): Promise<boolean> {
        return true;
    }

    dispose(): void {
        console.log('GLSPServerUtilServerNode - disposed!');
    }

    getClient(): CincoLoggingClient | undefined {
        return this.client;
    }

    setClient(client: CincoLoggingClient | undefined): void {
        this.client = client;
    }

    async getLog(): Promise<Map<'BACKEND' | 'SERVER', string[]>> {
        const logs = await this.logger.getFullLog();
        const result = new Map();
        result.set('BACKEND', logs.get('BACKEND'));
        result.set('SERVER', logs.get('SERVER'));
        return result;
    }

    registerLogging(): void | undefined {
        this.logger.addListener((msg: string, type: string) => {
            if (type === 'BACKEND' || type === 'SERVER') {
                this.client?.forward(msg, type);
            }
        });
        return;
    }
}
