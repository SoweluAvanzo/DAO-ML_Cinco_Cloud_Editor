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
import { injectable } from 'inversify';
import { CincoGraphModel } from './model';
import { GModelRoot, ISModelRootListener } from '@eclipse-glsp/client';

@injectable()
export class GraphModelProvider implements ISModelRootListener {
    private _model: Readonly<CincoGraphModel>;
    private _locked: ((graphModel: Readonly<CincoGraphModel>) => void)[] = [];

    modelRootChanged(root: Readonly<GModelRoot>): void {
        if (root instanceof CincoGraphModel) {
            this.graphModel = root;
        }
    }

    get graphModel(): Promise<Readonly<CincoGraphModel>> {
        return new Promise<Readonly<CincoGraphModel>>((resolve, reject) => {
            if (this._model) {
                resolve(this._model);
            } else {
                this._locked.push(resolve);
            }
        });
    }

    get isLoaded(): boolean {
        return this._model !== undefined;
    }

    set graphModel(model: Readonly<CincoGraphModel>) {
        this._model = model;
        this.unlockAll(this._model);
    }

    protected unlockAll(graphModel: Readonly<CincoGraphModel>): void {
        this._model = graphModel;
        for (const unlock of this._locked) {
            unlock(graphModel);
        }
        this._locked = [];
    }
}
