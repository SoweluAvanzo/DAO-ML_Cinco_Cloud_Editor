/********************************************************************************
 * Copyright (c) 2020-2022 Cinco Cloud and others.
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
import { MetaSpecification, MetaSpecificationResponseAction } from '@cinco-glsp/cinco-glsp-common';
import { Action, IActionHandler, ICommand } from '@eclipse-glsp/client';
import { injectable } from 'inversify';

@injectable()
export class MetaSpecificationResponseHandler implements IActionHandler {
    static _unlock: () => void;
    static _meta_spec_loaded = new Promise<void>((resolve, reject) => {
        MetaSpecificationResponseHandler._unlock = resolve;
    });

    handle(action: MetaSpecificationResponseAction): void | Action | ICommand {
        const metaSpec = action.metaSpecification;
        if (action.clear) {
            MetaSpecification.clear();
        }
        // update meta-specification
        MetaSpecification.merge(metaSpec);
        MetaSpecificationResponseHandler._unlock();

        // update palette
        return {
            kind: 'enableToolPalette'
        };
    }
}
