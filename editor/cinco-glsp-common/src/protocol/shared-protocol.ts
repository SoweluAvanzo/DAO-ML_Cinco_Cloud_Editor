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
import { PaletteItem } from '@eclipse-glsp/protocol';
import { AnyObject, hasStringProp } from './type-utils';

/**
 * Action
 *
 * This is the base for actions that will be dispatched to the backend by the listeners of the action tool
 */

export interface ManagedBaseAction extends Action {
    kind: string;
    modelElementId: string;
}

/**
 * The following interfaces are imported from the Eclipse-GLSP and shared by client and server
 */

export interface Operation extends Action {
    /**
     * Discriminator property to make operations distinguishable from plain {@link Action}s.
     */
    isOperation: true;
}
export namespace Operation {
    export function is(object: any): object is Operation {
        return Action.is(object) && (object as any).isOperation === true;
    }

    /**
     * Typeguard function to check wether the given object is an {@link Operation} with the given `kind`.
     * @param object The object to check.
     * @param kind  The expected operation kind.
     * @returns A type literal indicating wether the given object is an operation with the given kind.
     */
    export function hasKind(object: any, kind: string): object is Operation {
        return Operation.is(object) && object.kind === kind;
    }
}
export interface Action {
    /**
     * Unique identifier specifying the kind of action to process.
     */
    kind: string;
}
export namespace Action {
    export function is(object: any): object is Action {
        return AnyObject.is(object) && hasStringProp(object, 'kind');
    }
    /**
     * Typeguard function to check wether the given object is an {@link Action} with the given `kind`.
     * @param object The object to check.
     * @param kind  The expected action kind.
     * @returns A type literal indicating wether the given object is an action with the given kind.
     */
    export function hasKind(object: any, kind: string): object is Action {
        return Action.is(object) && object.kind === kind;
    }
}

export const UPDATING_RACE_CONDITION_INDICATOR = {
    id: '...updating'
} as PaletteItem;
