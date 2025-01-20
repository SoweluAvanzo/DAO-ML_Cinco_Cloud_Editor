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
import { MetaSpecificationRequestAction, SYSTEM_ID } from '@cinco-glsp/cinco-glsp-common';
import { CommandHandler } from '@theia/core';
import { Action, GLSPClient } from '@eclipse-glsp/protocol';

export class MetaSpecificationReloadCommandHandler implements CommandHandler {
    protected readonly client: GLSPClient;

    constructor(client: GLSPClient) {
        this.client = client;
    }

    execute(): void {
        // request & reload metaspecification
        this.sendGLSPSystemAction(this.client, MetaSpecificationRequestAction.create(true));
    }

    sendGLSPSystemAction(client: GLSPClient, action: Action): void {
        client.sendActionMessage({
            clientId: SYSTEM_ID,
            action: action
        });
    }
}
