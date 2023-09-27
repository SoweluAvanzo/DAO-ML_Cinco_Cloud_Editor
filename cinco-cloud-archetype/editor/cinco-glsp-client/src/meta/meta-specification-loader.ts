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

import {
    MetaSpecification,
    MetaSpecificationReloadAction,
    MetaSpecificationRequestAction
} from '@cinco-glsp/cinco-glsp-common';
import { IActionDispatcher } from '@eclipse-glsp/client';
import { MetaSpecificationResponseHandler } from './meta-specification-response-handler';

export class MetaSpecificationLoader {
    static meta_specifications: any[] = [];

    static load(
        actionDispatcher: IActionDispatcher
    ): Promise<void> {
        return new Promise<void>((resolve, _) => {
            // used for standalone applications
            actionDispatcher.dispatch(MetaSpecificationRequestAction.create(true)).then(async _ => {
                await MetaSpecificationResponseHandler._meta_spec_loaded;
                resolve();
            });
        });
    }

    static clear(actionDispatcher: IActionDispatcher): void {
        MetaSpecification.clear();
        {
            // send a reload to the server
            const reloadAction = MetaSpecificationReloadAction.create([], true);
            actionDispatcher.dispatch(reloadAction);
        }
    }

    static parseContent(content: string): object | undefined {
        try {
            return JSON.parse(content);
            // Do something with the parsed data
        } catch (err) {
            console.error(err);
        }
        return undefined;
    }
}
