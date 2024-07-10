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

import { fetchMetaSpecification } from './minio/minio-handler';
import * as process from 'process';

function init(targetFolder: string) {
    fetchMetaSpecification(targetFolder);
}

function needHelp(): boolean {
    return !process.argv || process.argv.length <= 2 || hasArg('--help');
}

function hasArg(key: string): boolean {
    return getArgIndex(key) >= 0;
}

function getArgIndex(key: string): number {
    const args = process.argv;
    for (let i = 0; i < args.length; i++) {
        if (args[i] === key) {
            return i;
        }
    }
    return -1;
}

function getArgValue(key: string): string {
    const args = process.argv;
    const index = getArgIndex(key);
    return args[index + 1];
}

if (needHelp()) {
    console.log("Please execute with argument '--metaFolder <pathToFolder>'.");
} else {
    const metaFolder = getArgValue('--metaFolder');
    if (!metaFolder) {
        throw new Error("No metaFolder defined! Please execute with argument '--metaFolder <pathToFolder>'.");
    }
    init(metaFolder);
}
