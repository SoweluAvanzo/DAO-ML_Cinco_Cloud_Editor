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

import { Action } from './shared-protocol';
import { hasStringProp } from './type-utils';

export class TypedServerMessageAction implements Action {
    kind: typeof ServerDialogAction.KIND;
    type: string;
}

export namespace TypedServerMessageAction {
    export const KIND = 'serverMessage';

    export function is(object: any): object is TypedServerMessageAction {
        return Action.hasKind(object, KIND) && hasStringProp(object, 'type');
    }

    export function hasType(object: any, type: string): object is Action {
        return object.type === type && Action.is(object);
    }
}

export class TypedServerResponseAction implements Action {
    kind: typeof TypedServerResponseAction.KIND;
    type: string;
}

export namespace TypedServerResponseAction {
    export const KIND = 'serverDialogResponse';

    export function is(object: any): object is TypedServerResponseAction {
        return Action.hasKind(object, KIND) && hasStringProp(object, 'type');
    }

    export function hasType(object: any, type: string): object is Action {
        return object.type === type && Action.is(object);
    }
}

export interface ServerOutputAction extends TypedServerMessageAction {
    type: typeof ServerOutputAction.TYPE;

    /**
     * The name of the channel.
     */
    name: string;

    /**
     * Further details on the message.
     */
    message: string;

    /**
     * LogLevel of the message.
     */
    logLevel?: string;

    /**
     * decides if output widget will be shown.
     */
    show?: boolean;

    /**
     * Context
     */
    args?: any;
}

export namespace ServerOutputAction {
    export const KIND = TypedServerMessageAction.KIND;
    export const TYPE = 'serverOutput';

    export function is(object: any): object is ServerOutputAction {
        return hasStringProp(object, 'name') && hasStringProp(object, 'message') && TypedServerMessageAction.is(object);
    }

    export function create(name: string, message: string, options?: { show?: boolean; logLevel?: string; args?: any }): ServerOutputAction {
        return {
            kind: KIND,
            type: TYPE,
            name,
            message,
            ...options
        };
    }
}

export interface ServerDialogAction extends TypedServerMessageAction {
    type: typeof ServerDialogAction.TYPE;

    /**
     * The messageId for the mapping to a response callback.
     */
    messageId: string;

    /**
     * The title of the dialog.
     */
    title: string;

    /**
     * The message of the dialog.
     */
    message: string;

    /**
     * Context
     */
    args?: any;
}

export namespace ServerDialogAction {
    export const KIND = TypedServerMessageAction.KIND;
    export const TYPE = 'serverDialog';

    export function is(object: any): object is ServerDialogAction {
        return (
            hasStringProp(object, 'messageId') &&
            hasStringProp(object, 'title') &&
            hasStringProp(object, 'message') &&
            TypedServerMessageAction.is(object)
        );
    }

    export function create(messageId: string, title: string, message: string, options?: { args?: any }): ServerDialogAction {
        return {
            kind: KIND,
            type: TYPE,
            messageId,
            title,
            message,
            ...options
        };
    }
}

export interface ServerDialogResponse extends TypedServerResponseAction {
    type: typeof ServerDialogResponse.TYPE;

    /**
     * Id of the Dialog
     */
    messageId: string;

    /**
     * Result of the Dialog
     */
    result: string;

    /**
     * Context
     */
    args?: any;
}

export namespace ServerDialogResponse {
    export const KIND = TypedServerResponseAction.KIND;
    export const TYPE = 'serverDialogResponse';

    export function is(object: any): object is ServerDialogResponse {
        return hasStringProp(object, 'messageId') && hasStringProp(object, 'result') && TypedServerMessageAction.is(object);
    }

    export function create(messageId: string, result: string, options?: { args?: any }): ServerDialogResponse {
        return {
            kind: KIND,
            type: TYPE,
            messageId,
            result,
            ...options
        };
    }
}
