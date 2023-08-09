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
import {
    CincoCloudModelValidationWidgetCommand,
    CincoCloudProjectValidationWidgetCommand
} from '@cinco-glsp/cinco-glsp-server/lib/src/shared/protocol/validation-protocol';
import { MenuContribution, MenuModelRegistry } from '@theia/core';
import { AbstractViewContribution, CommonMenus } from '@theia/core/lib/browser';
import { Command, CommandRegistry } from '@theia/core/lib/common/command';
import { injectable } from 'inversify';
import { CincoCloudModelValidationWidget } from './validation-model-widget';
import { CincoCloudProjectValidationWidget } from './validation-project-widget';

export class CincoCloudModelValidationWidgetContribution extends AbstractViewContribution<CincoCloudModelValidationWidget> {
    override toggleCommand: Command = CincoCloudModelValidationWidgetCommand;

    constructor() {
        super({
            widgetId: CincoCloudModelValidationWidget.ID,
            widgetName: CincoCloudModelValidationWidget.LABEL,
            defaultWidgetOptions: {
                area: 'bottom'
            },
            toggleCommandId: CincoCloudModelValidationWidgetCommand.id
        });
    }

    override registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(CincoCloudModelValidationWidgetCommand, {
            execute: () => super.openView({ activate: true, reveal: true })
        });
    }
}

export class CincoCloudProjectValidationWidgetContribution extends AbstractViewContribution<CincoCloudProjectValidationWidget> {
    override toggleCommand: Command = CincoCloudProjectValidationWidgetCommand;

    constructor() {
        super({
            widgetId: CincoCloudProjectValidationWidget.ID,
            widgetName: CincoCloudProjectValidationWidget.LABEL,
            defaultWidgetOptions: {
                area: 'bottom'
            },
            toggleCommandId: CincoCloudProjectValidationWidgetCommand.id
        });
    }

    override registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(CincoCloudProjectValidationWidgetCommand, {
            execute: () => super.openView({ activate: true, reveal: true })
        });
        commands.registerCommand(
            {
                id: 'validationTestCommand',
                label: 'Validate Test Command'
            },
            {
                execute: () => console.log('success')
            }
        );
    }
}

@injectable()
export class ValidationModelMenuContribution implements MenuContribution {
    registerMenus(menus: MenuModelRegistry): void {
        menus.registerMenuAction(CommonMenus.EDIT_FIND, {
            commandId: 'validationRequestModel',
            label: 'Validate Model'
        });
    }
}
