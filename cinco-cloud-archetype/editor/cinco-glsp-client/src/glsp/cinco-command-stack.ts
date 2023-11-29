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

import { GLSPCommandStack, GModelRoot } from '@eclipse-glsp/client';
import { injectable } from 'inversify';
import { CincoGraphModel } from '../model/model';

@injectable()
export class CincoGLSPCommandStack extends GLSPCommandStack {
    _locks: ((g: CincoGraphModel) => void)[] = [];

    get waitForCincoModel(): Promise<CincoGraphModel> {
        return new Promise<CincoGraphModel>(resolve => {
            if (this.currentModel instanceof CincoGraphModel) {
                resolve(this.currentModel);
            } else {
                this._locks.push(resolve);
            }
        });
    }

    protected override get currentModel(): Promise<GModelRoot> {
        return this.currentPromise.then(state => state.main.model);
    }

    protected override notifyListeners(root: Readonly<GModelRoot>): void {
        if (!(this.currentModel instanceof CincoGraphModel) && root instanceof CincoGraphModel) {
            for (const lock of this._locks) {
                lock(root);
            }
            this._locks = [];
        }
        this.onModelRootChangedEmitter.fire(root);
    }
}
