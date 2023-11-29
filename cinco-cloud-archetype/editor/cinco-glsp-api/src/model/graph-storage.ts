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

import { getGraphModelOfFileType } from '@cinco-glsp/cinco-glsp-common';
import { MaybePromise, RequestModelAction, SaveModelAction } from '@eclipse-glsp/server';
import { AbstractJsonModelStorage } from '@eclipse-glsp/server/lib/node/abstract-json-model-storage';
import { inject, injectable } from 'inversify';
import { GraphModel } from './graph-model';
import { GraphModelState } from './graph-model-state';

@injectable()
export class GraphModelStorage extends AbstractJsonModelStorage {
    @inject(GraphModelState)
    protected override modelState: GraphModelState;

    loadSourceModel(action: RequestModelAction): MaybePromise<void> {
        const sourceUri = this.getSourceUri(action);
        let graphModel = this.loadFromFile(sourceUri, GraphModel.is);
        graphModel = this.modelState.fixMissingProperties(graphModel, sourceUri);
        this.modelState.graphModel = graphModel;
    }

    saveSourceModel(action: SaveModelAction): MaybePromise<void> {
        const sourceUri = this.getFileUri(action);
        const serializableModel = this.modelState.sourceModel;
        this.writeFile(sourceUri, serializableModel);
    }

    protected override createModelForEmptyFile(path: string): GraphModel {
        const fileTypeIndex = path.lastIndexOf('.') + 1;
        const fileType = path.substring(fileTypeIndex);
        const spec = getGraphModelOfFileType(fileType);
        const graphModel = new GraphModel();
        if (spec) {
            graphModel.type = spec?.elementTypeId;
        }
        return graphModel;
    }
}
