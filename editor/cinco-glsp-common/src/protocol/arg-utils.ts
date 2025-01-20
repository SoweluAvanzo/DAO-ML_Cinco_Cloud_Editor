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

export function getArgs(key: string, marker = true): string | undefined {
    key = marker ? '--' + key : key;
    const args = process.argv.filter(a => a.startsWith(key));
    if (args.length > 0) {
        const result = args[0].substring(key.length + 1, undefined);
        if (result) {
            return result.replace(/"|'/g, ''); // replace quotes
        }
    }
    return undefined;
}

export function hasArg(argsKey: string, marker = true): boolean {
    argsKey = marker ? '--' + argsKey : argsKey;
    const args = process.argv.filter(a => a.startsWith(argsKey));
    return args.length > 0;
}
