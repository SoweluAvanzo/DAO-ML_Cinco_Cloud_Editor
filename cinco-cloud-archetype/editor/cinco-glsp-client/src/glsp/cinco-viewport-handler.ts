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

import { RestoreViewportHandler } from '@eclipse-glsp/client/lib/features/viewport/viewport-handler';
import { injectable, inject } from 'inversify';
import { GraphModelProvider } from '../model/graph-model-provider';

@injectable()
export class CincoRestoreViewportHandler extends RestoreViewportHandler {
    @inject(GraphModelProvider)
    protected readonly graphModelProvider: GraphModelProvider;

    protected async cincoGraphSelector(): Promise<string> {
        const graphModel = await this.graphModelProvider.graphModel;
        return `[data-svg-metadata-type="${graphModel.type}"]`;
    }

    override async postRequestModel(): Promise<void> {
        const graphSelector = await this.cincoGraphSelector();
        await this.waitForElement(graphSelector);
        await this.focusGraph();
    }

    override async focusGraph(): Promise<void> {
        const graphSelector = await this.cincoGraphSelector();
        if (this.focusTracker.hasFocus) {
            const container = this.focusTracker.diagramElement?.querySelector<HTMLElement>(graphSelector);
            container?.focus();
        }
    }
}
