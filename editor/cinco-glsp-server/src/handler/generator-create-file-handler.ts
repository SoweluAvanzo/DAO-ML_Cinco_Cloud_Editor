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
import { GeneratorCreateFileOperation } from '@cinco-glsp/cinco-glsp-common';
import { injectable } from 'inversify';
import { CincoJsonOperationHandler } from './cinco-json-operation-handler';
import * as fs from 'fs-extra';

@injectable()
export class GeneratorCreateFileHandler extends CincoJsonOperationHandler {
    operationType = GeneratorCreateFileOperation.KIND;

    executeOperation(operation: GeneratorCreateFileOperation): void {
        const contents = Array.from(operation.filesContentsMap.entries());
        for (const el of contents) {
            fs.writeFileSync(el[0]['codeUri']['path'], el[1]);
        }
    }
}
