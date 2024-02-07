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

import { CommandService } from '@theia/core';
import { MetaSpecificationRequestAction } from '@cinco-glsp/cinco-glsp-common';
import { GLSPActionDispatcher, IActionDispatcher } from '@eclipse-glsp/client';
import { MetaSpecificationResponseHandler } from './meta-specification-response-handler';

export class MetaSpecificationLoader {
    static async load(actionDispatcher: IActionDispatcher, commandService?: CommandService): Promise<void> {
        if (!(actionDispatcher instanceof GLSPActionDispatcher)) {
            throw Error('ActionDispatcher is not a GLSPActionDispatcher. The API must have been changed, please review!');
        }
        const response = await actionDispatcher.request(MetaSpecificationRequestAction.create());
        MetaSpecificationResponseHandler.handleResponse(response, commandService);
    }
}
