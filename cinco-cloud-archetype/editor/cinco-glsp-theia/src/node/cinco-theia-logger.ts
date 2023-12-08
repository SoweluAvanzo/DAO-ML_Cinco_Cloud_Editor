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

import { LogLevel, Logger } from '@theia/core';
import { injectable } from '@theia/core/shared/inversify';

@injectable()
export class CincoLogger extends Logger {
    protected static listeners: ((log: string) => void)[] = [];
    protected static loggedTerminal: { logLevel: number; log: string }[] = [];
    override log(logLevel: number, arg2: any, ...params: any[]): Promise<void> {
        CincoLogger.loggedTerminal.push({ logLevel: logLevel, log: arg2 });
        for (const listener of CincoLogger.listeners) {
            listener(this.toLoggedString(logLevel, arg2));
        }
        return super.log(logLevel, arg2, params);
    }

    logGLSPServer(logLevel: number, arg2: any, ...params: any[]): Promise<void> {
        return this.logChildProcess('GLSPServer', logLevel, arg2, params);
    }

    logChildProcess(processName: string, logLevel: number, arg2: any, ...params: any[]): Promise<void> {
        const message = `${processName}: ` + arg2;
        CincoLogger.loggedTerminal.push({ logLevel: logLevel, log: message });
        for (const listener of CincoLogger.listeners) {
            listener(this.toLoggedString(logLevel, message));
        }
        return super.log(logLevel, message, params);
    }

    async getFullLog(loglevel?: number): Promise<string> {
        const logs = CincoLogger.loggedTerminal.filter(l => (loglevel ? l.logLevel === loglevel : true));
        return logs.map(l => this.toLoggedString(l.logLevel, l.log)).join('\n');
    }

    toLoggedString(logLevel: number, log: string): string {
        return `${LogLevel.toString(logLevel)} ` + log;
    }

    addListener(listener: (log: string) => void): void {
        CincoLogger.listeners.push(listener);
    }
}
