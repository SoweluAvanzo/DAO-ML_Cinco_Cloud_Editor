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
import { Node, AbstractNodeHook, LanguageFilesRegistry, Container } from '@cinco-glsp/cinco-glsp-api';
import { CreateNodeOperation, Point } from '@eclipse-glsp/server';

export class ActivityHook2 extends AbstractNodeHook {
    override CHANNEL_NAME: string | undefined = 'ActivityHook2 [' + this.modelState.root.id + ']';

    /**
     * Create
     */

    override canCreate(operation: CreateNodeOperation): boolean {
        this.log('Triggered canCreate. Can create node of type: ' + operation.elementTypeId);
        return true;
    }

    // This should not be triggered in this test
    override preCreate(container: Container, location: Point | undefined): void {
        this.log('Triggered preCreate. Creating node in container (' + container.id + ') at position (' + location + ')');
    }

    override postCreate(node: Node): void {
        this.log('Triggered postCreate on node (' + node.id + ')');
        throw Error("This is a test error in the 'postCreate' hook of ActivityHooks2");
    }
}

LanguageFilesRegistry.register(ActivityHook2);
