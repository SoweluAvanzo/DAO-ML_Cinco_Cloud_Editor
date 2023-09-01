/********************************************************************************
 * Copyright (c) 2022 Cinco Cloud.
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
    META_FILE_TYPES, MetaSpecification, MetaSpecificationReloadAction, MetaSpecificationResponseAction
} from '@cinco-glsp/cinco-glsp-common';
import { Action, ActionHandler, MaybePromise } from '@eclipse-glsp/server-node';
import { injectable } from 'inversify';
import { MetaSpecificationLoader } from '../meta/meta-specification-loader';
import { getLanguageFolder } from '@cinco-glsp/cinco-glsp-api';

@injectable()
export class MetaSpecificationReloadHandler implements ActionHandler {

    actionKinds: string[] = [MetaSpecificationReloadAction.KIND];

    execute(action: MetaSpecificationReloadAction, ...args: unknown[]): MaybePromise<Action[]> {
        const items = action.items;
        const clear = action.clear ?? false;
        if (clear) {
            MetaSpecificationLoader.clear();
        }
        if(items === undefined || items.length <= 0) { // default behaviour
            const folderPath = getLanguageFolder();
            const supportedFileTypes = META_FILE_TYPES;
            MetaSpecificationLoader.load(supportedFileTypes, folderPath);
        } else {
            for (const item of items) {
                const folderPaths = item.folderPaths;
                const supportedFileTypes = item.supportedFileTypes;
                for (const folderPath of folderPaths) {
                    MetaSpecificationLoader.load(supportedFileTypes, folderPath);
                }
            }
        }
        // forward the update to the clients, after reload
        return [MetaSpecificationResponseAction.create(MetaSpecification.get())];
    }
}
