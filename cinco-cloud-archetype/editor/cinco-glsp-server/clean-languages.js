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

const path = require('path');
const fs = require('fs');
const langSrc = path.resolve(__dirname, 'languages');

// if langSrc does not exists, the structure can look different, because of webpack.
// thus, if there is no langSrc, or there is no file to include in it, we need to create
// a dummy file, to keep the folder-structure
if (!fs.existsSync(langSrc)) {
    fs.mkdirSync(langSrc);
}
const containableFiles = fs.readdirSync(langSrc).length;
if (containableFiles <= 0) {
    console.log('languages-folder contains no relevant files...');
    const content = '// this is a dummy-file to preserve the folder-structure';
    fs.writeFileSync(langSrc + '/dummy.ts', content, function (err) {
        if (err) {
            return console.log(err);
        }
        console.log('...dummy file was written!');
    });
    console.log('closing: ' + fs.realpathSync(langSrc));
}
