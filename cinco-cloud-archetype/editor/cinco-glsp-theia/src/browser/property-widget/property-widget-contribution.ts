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
import { CincoCloudPropertyWidgetCommand } from '@cinco-glsp/cinco-glsp-common/lib/protocol/property-protocol';
import { AbstractViewContribution } from '@theia/core/lib/browser';
import { Command, CommandRegistry } from '@theia/core/lib/common/command';

import { CincoCloudPropertyWidget } from './property-widget';

export class CincoCloudPropertyWidgetContribution extends AbstractViewContribution<CincoCloudPropertyWidget> {
    override toggleCommand: Command = CincoCloudPropertyWidgetCommand;

    constructor() {
        super({
            widgetId: CincoCloudPropertyWidget.ID,
            widgetName: CincoCloudPropertyWidget.LABEL,
            defaultWidgetOptions: {
                area: 'bottom'
            },
            toggleCommandId: CincoCloudPropertyWidgetCommand.id
        });
    }

    override registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(CincoCloudPropertyWidgetCommand, {
            execute: () => super.openView({ activate: true, reveal: true })
        });
    }
}
