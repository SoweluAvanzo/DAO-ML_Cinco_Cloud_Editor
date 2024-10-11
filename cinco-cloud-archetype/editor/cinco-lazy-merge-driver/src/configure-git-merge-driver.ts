/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
import { spawn } from 'child_process';
import { exit } from 'process';
import * as path from 'path';

main();

async function main(): Promise<void> {
    try {
        await configureDriver('cinco-lazy-merge', { eager: false, arbitrarily: false });
        await configureDriver('cinco-eager-merge', { eager: true, arbitrarily: false });
        await configureDriver('cinco-lazy-merge-arbitrarily', { eager: false, arbitrarily: true });
        await configureDriver('cinco-eager-merge-arbitrarily', { eager: true, arbitrarily: true });
    } catch (error) {
        console.log(error);
        exit(1);
    }
}

function configureDriver(name: string, { eager, arbitrarily }: { eager: boolean; arbitrarily: boolean }): Promise<void> {
    return new Promise((resolve, reject) => {
        const process = spawn(
            'git',
            [
                'config',
                '--global',
                `merge.${name}.driver`,
                `node '${path.join(__dirname, 'cinco-lazy-merge.js')}' %O %A %B %A ` +
                    '--conflict-marker-size=%L' +
                    (eager ? ' --fail-merge-on-lazy-conflicts' : '') +
                    (arbitrarily ? ' --merge-unknown-cells-arbitrarily' : '')
            ],
            { stdio: 'inherit' }
        );

        process.on('error', error => {
            reject(error);
        });

        process.on('exit', code => {
            if (code === 0) {
                resolve();
            } else {
                reject(new Error(`git command exited with ${code}`));
            }
        });
    });
}
