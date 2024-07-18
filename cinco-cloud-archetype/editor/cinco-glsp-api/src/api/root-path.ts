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

import { getWorkspaceRootUri, getLanguageFolder } from '../utils/file-helper';
import * as path from 'path';

export class RootPath {
    static readonly WORKSPACE = new RootPath('WORKSPACE', getWorkspaceRootUri);
    static readonly LANGUAGES = new RootPath('LANGUAGES', getLanguageFolder);

    readonly name: string;
    private readonly pathFunction: () => string;

    private constructor(name: string, pathFunction: () => string) {
        this.name = name;
        this.pathFunction = pathFunction;
    }

    get path(): string {
        return path.normalize(this.pathFunction());
    }

    join(relativePath: string): string {
        return path.join(this.path, relativePath);
    }
}
