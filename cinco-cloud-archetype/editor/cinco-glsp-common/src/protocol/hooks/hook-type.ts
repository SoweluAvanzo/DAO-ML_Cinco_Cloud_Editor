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

export enum HookType {
    CAN_ATTRIBUTE_CHANGE = 'CanAttributeChange',
    PRE_ATTRIBUTE_CHANGE = 'PreAttributeChange',
    POST_ATTRIBUTE_CHANGE = 'PostAttributeChange',
    CAN_CREATE = 'CanCreate',
    PRE_CREATE = 'PreCreate',
    POST_CREATE = 'PostCreate',
    CAN_DOUBLE_CLICK = 'CanDoubleClick',
    POST_DOUBLE_CLICK = 'PostDoubleClick',
    CAN_DELETE = 'CanDelete',
    PRE_DELETE = 'PreDelete',
    POST_DELETE = 'PostDelete',
    CAN_MOVE = 'CanMove',
    PRE_MOVE = 'PreMove',
    POST_MOVE = 'PostMove',
    CAN_RESIZE = 'CanResize',
    PRE_RESIZE = 'PreResize',
    POST_RESIZE = 'PostResize',
    CAN_SELECT = 'CanSelect',
    POST_SELECT = 'PostSelect',
    CAN_RECONNECT = 'CanReconnect',
    PRE_RECONNECT = 'PreReconnect',
    POST_RECONNECT = 'PostReconnect',
    POST_PATH_CHANGE = 'PostPathChange',
    POST_CONTENT_CHANGE = 'PostContentChange',
    CAN_SAVE = 'CanSave',
    POST_SAVE = 'PostSave'
}
