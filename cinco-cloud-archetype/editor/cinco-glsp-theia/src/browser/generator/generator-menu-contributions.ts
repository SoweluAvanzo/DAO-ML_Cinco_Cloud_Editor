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
import { GenerateGraphDiagramCommand } from '@cinco-glsp/cinco-glsp-common';
import { KeybindingContribution, KeybindingRegistry } from '@theia/core/lib/browser';
import { MenuContribution, MenuModelRegistry } from '@theia/core/lib/common';
import { injectable } from '@theia/core/shared/inversify';
import { GLSPDiagramMenus } from '@eclipse-glsp/theia-integration';

@injectable()
export class GenerateGraphDiagramMenuContribution implements MenuContribution {
    registerMenus(menus: MenuModelRegistry): void {
        menus.registerMenuAction(GLSPDiagramMenus.DIAGRAM, {
            commandId: GenerateGraphDiagramCommand.id,
            label: GenerateGraphDiagramCommand.label
        });
    }
}

@injectable()
export class GenerateGraphDiagramKeybindingContribution implements KeybindingContribution {
    registerKeybindings(keybindings: KeybindingRegistry): void {
        keybindings.registerKeybinding({
            keybinding: GenerateGraphDiagramCommand.keybinding,
            command: GenerateGraphDiagramCommand.id,
            when: 'cincoGraphModelType !== undefined'
        });
    }
}
