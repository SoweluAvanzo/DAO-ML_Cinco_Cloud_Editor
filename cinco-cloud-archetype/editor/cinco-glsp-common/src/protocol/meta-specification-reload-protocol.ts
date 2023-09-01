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

import { CompositionSpecification } from '../meta-specification';
import { Action } from './shared-protocol';

export interface FileProviderResponse extends Action {
    kind: typeof FileProviderResponse.KIND;
    items: FileProviderResponseItem[];
}

export namespace FileProviderResponse {
    export const KIND = 'fileprovider.response';

    export function create(items: FileProviderResponseItem[]): FileProviderResponse {
        return {
            kind: KIND,
            items
        };
    }
}

export interface FileProviderResponseItem {
    path: string;
    content: string | undefined;
}

export namespace FileProviderResponseItem {
    export function create(path: string, content: string | undefined): FileProviderResponseItem {
        return {
            path,
            content
        };
    }
}

export interface FileProviderRequest extends Action {
    kind: typeof FileProviderRequest.KIND;
    directories: string[];
    readFiles?: boolean;
    supportedTypes: string[];
}

export namespace FileProviderRequest {
    export const KIND = 'fileprovider.request';

    export function create(directories: string[], readFiles?: boolean, supportedTypes: string[] = []): FileProviderRequest {
        return {
            kind: KIND,
            directories,
            readFiles,
            supportedTypes
        };
    }
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

export interface MetaSpecificationRequestAction extends Action {
    kind: typeof MetaSpecificationRequestAction.KIND;
    reload?: boolean;
}

export namespace MetaSpecificationRequestAction {
    export const KIND = 'meta-specification.request';

    export function create(reload?: boolean): MetaSpecificationRequestAction {
        return {
            kind: KIND,
            reload
        };
    }
}

export interface MetaSpecificationResponseAction extends Action {
    kind: typeof MetaSpecificationResponseAction.KIND;
    metaSpecification: CompositionSpecification;
}

export namespace MetaSpecificationResponseAction {
    export const KIND = 'meta-specification.response';

    export function create(metaSpecification: CompositionSpecification): MetaSpecificationResponseAction {
        return {
            kind: KIND,
            metaSpecification
        };
    }
}

export namespace MetaSpecificationReloadCommand {
    export const ID = 'cinco.meta-specification.reload';
}
