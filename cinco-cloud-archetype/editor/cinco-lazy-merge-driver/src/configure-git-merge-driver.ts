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

const process = spawn(
    'git',
    ['config', '--global', 'merge.cinco-lazy-merge.driver', `node '${__dirname}/cinco-lazy-merge.js' %O %A %B %L %A`],
    { stdio: 'inherit' }
);

process.on('error', error => {
    console.error(error);
    exit(1);
});

process.on('exit', code => {
    if (code !== 0) {
        console.error(`git command exited with ${code}`);
        exit(1);
    }
});
