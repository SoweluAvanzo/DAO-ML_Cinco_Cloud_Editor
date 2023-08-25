/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud and others.
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
import { MetaSpecificationUpdateAction } from '@cinco-glsp/cinco-glsp-common';
import { BaseGLSPTool } from '@eclipse-glsp/client';
import { injectable, postConstruct } from 'inversify';

@injectable()
export class UpdateMetaSpecificationTool extends BaseGLSPTool {
    static readonly ID = 'update-meta-specification-tool';

    @postConstruct()
    initGeneratorAction(): void {
        window.addEventListener('message', ({ data: message }: { data: MetaSpecificationUpdateAction }) => {
            if (message.kind === 'meta-specification.update') {
                console.log("Recevied window event to update meta specification");
                const action = MetaSpecificationUpdateAction.create(message.metaSpecification);
                this.actionDispatcher.dispatch(action);
                console.log("Dispacted action to update meta specification");
            }
        });
    }

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    enable(): void {}

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    disable(): void {}

    get id(): string {
        return UpdateMetaSpecificationTool.ID;
    }
}
