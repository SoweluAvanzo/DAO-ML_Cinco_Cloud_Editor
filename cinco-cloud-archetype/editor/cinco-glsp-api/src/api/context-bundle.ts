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

import { Logger, ActionDispatcher, SourceModelStorage, ModelSubmissionHandler } from '@eclipse-glsp/server';
import { GraphModelState } from '../model/graph-model-state';

export class ContextBundle {
    readonly logger: Logger;
    readonly modelState: GraphModelState;
    readonly actionDispatcher: ActionDispatcher;
    readonly sourceModelStorage: SourceModelStorage;
    readonly submissionHandler: ModelSubmissionHandler;

    constructor(
        modelState: GraphModelState,
        logger: Logger,
        actionDispatcher: ActionDispatcher,
        sourceModelStorage: SourceModelStorage,
        submissionHandler: ModelSubmissionHandler
    ) {
        this.logger = logger;
        this.modelState = modelState;
        this.actionDispatcher = actionDispatcher;
        this.sourceModelStorage = sourceModelStorage;
        this.submissionHandler = submissionHandler;
    }
}
