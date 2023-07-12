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
import { CreateNodeOperationHandler, Operation } from '@eclipse-glsp/server-node';
import { injectable } from 'inversify';
import { ElementType, getSpecOf } from '../shared/meta-specification';

@injectable()
export class SpecifiedElementHandler extends CreateNodeOperationHandler {
    _specification: ElementType | undefined;
    BLACK_LIST: string[] = [];

    get elementTypeId(): string | undefined {
        return this.elementTypeIds.length > 0 ? this.elementTypeIds[0] : undefined;
    }

    get elementTypeIds(): string[] {
        return [];
    }

    get specification(): ElementType | undefined {
        return this._specification;
    }

    set specification(spec: ElementType | undefined) {
        this._specification = spec;
    }

    execute(operation: Operation): void {
        throw new Error('Method not implemented.');
    }

    override get operationType(): string {
        throw new Error('Method not implemented.');
    }

    get label(): string {
        return this.specification!.label;
    }

    getLabelFor(elementTypeId: string): string {
        return getSpecOf(elementTypeId)?.label ?? elementTypeId;
    }
}
