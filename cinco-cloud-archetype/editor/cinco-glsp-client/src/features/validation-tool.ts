/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud and others.
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
import { ValidationModelResponseAction, ValidationRequestAction } from '@cinco-glsp/cinco-glsp-common';
import { Action, BaseGLSPTool, IActionHandler, ICommand } from '@eclipse-glsp/client';
import { CommandService } from '@theia/core';
import { inject, injectable, optional, postConstruct } from 'inversify';

@injectable()
export class ValidationTool extends BaseGLSPTool {
    @inject(CommandService) @optional() commandService: CommandService;

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    enable(): void {}

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    disable(): void {}

    static readonly ID = 'validation-tool';

    @postConstruct()
    registerValidationToolCommand(): void {
        if (this.commandService)
            this.commandService.executeCommand('registerFromGLSP2Theia', {
                commandId: 'validationRequestModel',
                callback: () => {
                    this.sendValidationRequest();
                }
            });
    }

    sendValidationRequest(): void {
        const action = ValidationRequestAction.create(this.editorContext.modelRoot.id);
        this.actionDispatcher.dispatch(action);
    }

    get id(): string {
        return ValidationTool.ID;
    }
}

@injectable()
export class ValidationModelResponseActionHandler implements IActionHandler {
    @inject(CommandService) @optional() commandService: CommandService;

    handle(action: ValidationModelResponseAction): void | Action | ICommand {
        if (this.commandService) this.commandService.executeCommand('CincoCloud.updateValidationModel', action.messages);
    }
}
