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
import { Command, CommandRegistry } from '@theia/core';
import { AbstractViewContribution } from '@theia/core/lib/browser/shell/view-contribution';
import { injectable } from '@theia/core/shared/inversify';
import { CincoProjectInitializerWidget } from './cinco-project-initializer-widget';

export const CincoProjectInitializerWidgetCommand: Command = {
    id: 'cincoCloudProjectInitializer:open',
    label: 'Cinco Cloud: Project Initializer'
};

@injectable()
export class CincoProjectInitializerWidgetContribution extends AbstractViewContribution<CincoProjectInitializerWidget> {
    constructor() {
        super({
            widgetId: CincoProjectInitializerWidget.ID,
            widgetName: CincoProjectInitializerWidget.LABEL,
            defaultWidgetOptions: { area: 'main' },
            toggleCommandId: CincoProjectInitializerWidgetCommand.id
        });
    }

    override registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(CincoProjectInitializerWidgetCommand, {
            execute: () => super.openView({ activate: false, reveal: true })
        });
    }
}
