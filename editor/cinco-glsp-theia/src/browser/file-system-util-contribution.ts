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

import { FrontendApplicationContribution } from '@theia/core/lib/browser';
import { inject, injectable } from 'inversify';
import { FilesystemUtilServer } from '../common/file-system-util-protocol';

/**
 * This class is needed for the initial connection of the fsUtils
 */

@injectable()
export class FileSystemUtilService implements FrontendApplicationContribution {
    @inject(FilesystemUtilServer) fsUtils: FilesystemUtilServer;

    initialize(): void {
        // intialize connection
        this.fsUtils
            .connect()
            .then(_ => console.log('*** file system utils connected ***'))
            .catch(error => console.log('*** Failed to start file system utils: "' + error + '" ***'));
    }
}
