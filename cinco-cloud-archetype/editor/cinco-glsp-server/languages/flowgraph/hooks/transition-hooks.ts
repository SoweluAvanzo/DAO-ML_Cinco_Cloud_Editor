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
import { CreateEdgeOperation } from '@eclipse-glsp/server';
import { Edge, AbstractEdgeHooks, LanguageFilesRegistry } from '@cinco-glsp/cinco-glsp-api';

export class TransitionHooks extends AbstractEdgeHooks {
    static override typeId = 'edge:transition';
    static override hookName = 'TransitionHooks';
    static override hookTypes = ['PreDelete', 'CanDelete', 'PostDelete', 'CanCreate'];
    override preDelete(edge: Edge): void {
        this.logger.info('Deleting Edge: ' + edge.id);
    }

    override canDelete(edge: Edge): boolean {
        this.logger.info('Trying to delete Edge: ' + edge.id);
        return true;
    }

    override postDelete(edge: Edge): void {
        this.logger.info('Deleted Edge: ' + edge.id);
    }
    override canCreate(operation: CreateEdgeOperation): boolean {
        this.logger.info('Sure.');
        return true;
    }
}

TransitionHooks.register();
LanguageFilesRegistry.register(TransitionHooks);
