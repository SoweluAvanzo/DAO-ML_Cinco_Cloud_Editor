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
import { LanguageFilesRegistry, LabelProvider } from '@cinco-glsp/cinco-glsp-api';
import {
    LabelRequestAction
} from '@cinco-glsp/cinco-glsp-common';


/**
 * Language Designer defined example of a LabelProvider
 */
export class ExampleLabelProvider extends LabelProvider {
    override CHANNEL_NAME: string | undefined = 'LabelProvider [' + this.modelState.graphModel.id + ']';

    provide(action: LabelRequestAction, ...args: unknown[]): Promise<string> | string {
        this.log("LabelRequest: " + action.annotatedElementType);
        if (action.annotatedElementType == 'labeltest:labelprovidertestreference1') {
            return `Dynamically provided label by pointer`;
        } else if (action.annotatedElementType == 'labeltest:labelprovidertestinstance') {
            return `Dynamically provided label by instance`;
        }
        return 'Dynamically provided label!';
    }
}
// register into app
LanguageFilesRegistry.register(ExampleLabelProvider);
