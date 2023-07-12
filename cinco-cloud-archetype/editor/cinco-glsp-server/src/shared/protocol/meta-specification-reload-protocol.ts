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

import { Action } from './shared-protocol';

export interface FileProviderResponse {
    items: FileProviderResponseItem[];
}

export interface FileProviderResponseItem {
    path: string;
    content: string | undefined;
}

export interface MetaSpecificationReloadAction extends Action {
    kind: typeof MetaSpecificationReloadAction.KIND;
    items: MetaSpecificationReloadItem[];
    clear?: boolean;
}

export namespace MetaSpecificationReloadAction {
    export const KIND = 'meta-specification.reload';

    export function create(items: MetaSpecificationReloadItem[], clear?: boolean): MetaSpecificationReloadAction {
        return {
            kind: KIND,
            items,
            clear
        };
    }
}

export interface MetaSpecificationReloadItem {
    folderPaths: string[];
    supportedFileTypes: string[];
}
export namespace MetaSpecificationReloadItem {
    export function create(folderPaths: string[], supportedFileTypes: string[]): MetaSpecificationReloadItem {
        return {
            folderPaths,
            supportedFileTypes
        };
    }
}
