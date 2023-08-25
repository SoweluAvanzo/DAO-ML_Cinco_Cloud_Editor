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
import { getLibLanguageFolder } from '@cinco-glsp/cinco-glsp-api';
import { MetaSpecificationUpdateAction } from '@cinco-glsp/cinco-glsp-common';
import { Action, ActionDispatcher, ActionHandler, Logger, MaybePromise } from '@eclipse-glsp/server-node';
import { inject, injectable } from 'inversify';
import * as fs from 'fs';

@injectable()
export class MetaSpecificationUpdateHandler implements ActionHandler {
    @inject(Logger)
    readonly logger: Logger;
    @inject(ActionDispatcher)
    readonly actionDispatcher: ActionDispatcher;

    actionKinds: string[] = [MetaSpecificationUpdateAction.KIND];

    execute(action: MetaSpecificationUpdateAction, ...args: unknown[]): MaybePromise<Action[]> {
        console.log('Received action to update meta-specification');
        const targetPath = getLibLanguageFolder() + ('/meta-specification.json');
        fs.writeFileSync(targetPath, JSON.stringify(action.metaSpecification, undefined, 4));

        this.logger.info('Successfully received and processed meta specification update');
        return [];
    }
}
