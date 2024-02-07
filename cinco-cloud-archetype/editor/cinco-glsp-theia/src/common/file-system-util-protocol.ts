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
import { RpcServer } from '@theia/core/lib/common/messaging/proxy-factory';

export const FILESYSTEM_UTIL_ENDPOINT = 'services/cc_fs_util';
export const FilesystemUtilClient = Symbol('FilesystemUtilClient');
export interface FilesystemUtilClient {}
export const FilesystemUtilServer = Symbol('FilesystemUtilServer');

export interface FilesystemUtilServer extends RpcServer<FilesystemUtilClient> {
    connect(): Promise<boolean>;
    // provides all files of a specified absolute folder path
    getFiles(absFolderPath: string): Promise<string[]> | undefined;
    // reads contents of a files located at the given filePaths
    readFiles(filePaths: string[], encoding?: string): Promise<string[]>;
    // pushes something of any type to the client
    push(some: any): void;
}
