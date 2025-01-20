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

import { GraphType } from '../meta-specification';

export const EDITOR_BUTTON_REGISTRATION_COMMAND = { id: 'cinco-cloud.register-editor-buttons' };
export const EDITOR_BUTTON_UNREGISTRATION_COMMAND = { id: 'cinco-cloud.unregister-editor-buttons' };
export const CONTEXT_MENU_BUTTON_REGISTRATION_COMMAND = { id: 'cinco-cloud.register-context-menu-buttons' };
export const CONTEXT_MENU_BUTTON_UNREGISTRATION_COMMAND = { id: 'cinco-cloud.unregister-context-menu-buttons' };

// command to create a new model file
export const CREATE_NEW_MODEL_FILE_ID = 'cinco-cloud.create-file';

/* dynamic gui values */

export function getFileCreationLabel(g: GraphType): string {
    return 'Create' + ' ' + g.label + ' (*.' + g.diagramExtension + ')';
}

export function getFileCreationCommandId(g: GraphType): string {
    return CREATE_NEW_MODEL_FILE_ID + '.' + g.elementTypeId;
}
