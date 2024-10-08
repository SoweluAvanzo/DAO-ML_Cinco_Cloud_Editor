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
import { Node, AbstractNodeHook, LanguageFilesRegistry, Container, GraphModel } from '@cinco-glsp/cinco-glsp-api';
import { Point } from '@eclipse-glsp/server';

export class ActivityHook2 extends AbstractNodeHook {
    override CHANNEL_NAME: string | undefined = 'ActivityHook2 [' + this.modelState.graphModel.id + ']';

    /**
     * Create
     */

    override canCreate(elementTypeId: string, container: Container | GraphModel, location?: Point): boolean {
        this.log('Triggered preCreate. Can create node of type (' + elementTypeId + ') in container (' + container.id + ') at position (' + location + ')');
        return true;
    }

    // This should not be triggered in this test
    override preCreate(elementTypeId: string, container: Container | GraphModel, location?: Point): void {
        this.log('Triggered preCreate. Creating node of type (' + elementTypeId + ') in container (' + container.id + ') at position (' + location + ')');
    }

    override postCreate(node: Node): void {
        this.log('Triggered postCreate on node (' + node.id + ')');
        throw Error("This is a test error in the 'postCreate' hook of ActivityHooks2");
    }
}

LanguageFilesRegistry.register(ActivityHook2);
