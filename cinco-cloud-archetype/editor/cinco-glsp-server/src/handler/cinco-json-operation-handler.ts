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

import { GraphModelState } from '@cinco-glsp/cinco-glsp-api';
import { ContextBundle } from '@cinco-glsp/cinco-glsp-api/lib/api/context-bundle';
import {
    ActionDispatcher,
    Command,
    JsonOperationHandler,
    Logger,
    MaybePromise,
    ModelSubmissionHandler,
    Operation,
    SaveModelAction,
    SourceModelStorage
} from '@eclipse-glsp/server';
import { injectable, inject } from 'inversify';

@injectable()
export abstract class CincoJsonOperationHandler extends JsonOperationHandler {
    @inject(ActionDispatcher)
    protected readonly actionDispatcher: ActionDispatcher;
    @inject(GraphModelState)
    override readonly modelState: GraphModelState;
    @inject(Logger)
    protected readonly logger: Logger;
    @inject(SourceModelStorage)
    protected sourceModelStorage: SourceModelStorage;
    @inject(ModelSubmissionHandler)
    protected submissionHandler: ModelSubmissionHandler;

    getBundle(): ContextBundle {
        return new ContextBundle(this.modelState, this.logger, this.actionDispatcher, this.sourceModelStorage, this.submissionHandler);
    }

    createCommand(operation: Operation): MaybePromise<Command | undefined> {
        return this.commandOf(() => {
            this.executeOperation(operation);
        });
    }

    abstract executeOperation(operation: Operation): void;

    saveAndUpdate(): void {
        const graphmodel = this.modelState.index.getRoot();
        const fileUri = graphmodel._sourceUri;
        this.actionDispatcher.dispatch(SaveModelAction.create({ fileUri }));
    }
}
