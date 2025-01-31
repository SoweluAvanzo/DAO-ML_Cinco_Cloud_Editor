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
import {
    Action,
    EnableToolPaletteAction,
    GLSPActionDispatcher,
    IActionHandler,
    ICommand,
    TYPES,
    ToolPalette,
    UIExtensionRegistry
} from '@eclipse-glsp/client';
import { inject, injectable, postConstruct } from 'inversify';
import { CincoToolPalette } from './cinco-tool-palette';

@injectable()
export class CincoToolPaletteUpdateHandler implements IActionHandler {
    @inject(GLSPActionDispatcher)
    protected actionDispatcher: GLSPActionDispatcher;
    @inject(ToolPalette)
    protected cincoToolPalette: CincoToolPalette;
    @inject(TYPES.UIExtensionRegistry)
    protected readonly uiExtensionRegistry: UIExtensionRegistry;

    @postConstruct()
    postConstruct(): void {
        if (!this.uiExtensionRegistry.hasKey('tool-palette')) {
            this.uiExtensionRegistry.register('tool-palette', this.cincoToolPalette);
        }
    }

    handle(action: Action): void | Action | ICommand {
        if (action.kind === EnableToolPaletteAction.KIND) {
            CincoToolPalette.requestPalette(this.actionDispatcher);
        } else if (this.cincoToolPalette) {
            this.cincoToolPalette.handle(action);
        }
    }
}
