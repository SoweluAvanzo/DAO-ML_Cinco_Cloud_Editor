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
import { ValidationMessage } from '@cinco-glsp/cinco-glsp-common/lib/protocol/validation-protocol';
import { injectable } from 'inversify';

@injectable()
export class ValidationModelDataHandler {
    currentMessages: Map<string, ValidationMessage[]> = new Map();
    dataSubscriptions: (() => void)[] = [];

    updatePropertySelection(modelId: string, modelElementId: string, messages: ValidationMessage[]): void {
        const id = `${modelId}_${modelElementId}`;
        if (messages.length <= 0 && this.currentMessages.has(id)) {
            // empty messages means remove
            this.currentMessages.delete(id);
        } else {
            this.currentMessages.set(id, messages);
        }
        this.dataSubscriptions.forEach(fn => fn());
    }

    registerDataSubscription(callback: () => void): void {
        this.dataSubscriptions.push(callback);
    }
}
