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
import { inject, injectable } from 'inversify';
import { CincoGraphModel } from './model';
import { GModelRoot, IDiagramOptions, ISModelRootListener, TYPES } from '@eclipse-glsp/client';
import { URI } from '@cinco-glsp/cinco-glsp-common';

@injectable()
export class GraphModelProvider implements ISModelRootListener {
    @inject(TYPES.IDiagramOptions)
    protected options: IDiagramOptions;

    private static _model: Map<string, Readonly<CincoGraphModel>> = new Map();
    protected static _locked: Map<string, ((graphModel: Readonly<CincoGraphModel>) => void)[]> = new Map();

    modelRootChanged(root: Readonly<GModelRoot>): void {
        if (root instanceof CincoGraphModel) {
            this.graphModel = root;
        }
    }

    get graphModel(): Promise<Readonly<CincoGraphModel>> {
        return new Promise<Readonly<CincoGraphModel>>((resolve, reject) => {
            const currentSourceUri = this.getSourceUri();
            if (!currentSourceUri) {
                return;
            }
            const model = GraphModelProvider._model.get(currentSourceUri);
            if (model) {
                resolve(model);
            } else {
                if (!GraphModelProvider._locked.has(currentSourceUri)) {
                    GraphModelProvider._locked.set(currentSourceUri, []);
                }
                GraphModelProvider._locked.get(currentSourceUri)?.push(resolve);
            }
        });
    }

    getGraphModelFrom(sourceUri: string): Readonly<CincoGraphModel> | undefined {
        const uri = new URI(sourceUri).path.fsPath();
        const models = GraphModelProvider._model;
        const model = models.get(uri);
        return model;
    }

    get isLoaded(): boolean {
        return GraphModelProvider._model.get(this.options.sourceUri ?? '') !== undefined;
    }

    set graphModel(model: Readonly<CincoGraphModel>) {
        const currentSourceUri = this.getSourceUri();
        if (!currentSourceUri) {
            throw new Error('No sourceUri to relate given model!');
        }
        GraphModelProvider._model.set(currentSourceUri, model);
        this.unlockAll(GraphModelProvider._model.get(currentSourceUri)!);
    }

    protected unlockAll(graphModel: Readonly<CincoGraphModel>): void {
        const currentSourceUri = this.getSourceUri();
        if (!currentSourceUri) {
            return;
        }
        const toUnlock = GraphModelProvider._locked.get(currentSourceUri) ?? [];
        for (const unlock of toUnlock) {
            unlock(graphModel);
        }
        GraphModelProvider._locked.set(currentSourceUri, []);
    }

    getSourceUri(): string | undefined {
        if (!this.options.sourceUri) {
            return undefined;
        }
        return URI.fromFilePath(this.options.sourceUri).path.fsPath();
    }
}
