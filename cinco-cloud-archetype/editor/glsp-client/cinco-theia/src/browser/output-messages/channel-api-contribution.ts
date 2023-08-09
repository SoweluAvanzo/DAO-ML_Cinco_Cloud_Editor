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

import { Command, CommandHandler, CommandRegistry, MaybePromise } from '@theia/core';
import { FrontendApplication, FrontendApplicationContribution } from '@theia/core/lib/browser';
import { OutputChannel, OutputChannelManager } from '@theia/output/lib/browser/output-channel';
import { OutputCommands } from '@theia/output/lib/browser/output-commands';
import { inject, injectable } from 'inversify';

export const CREATE_CHANNEL: Command = { id: 'cinco:create_channel' };
export const SHOW_CHANNEL = OutputCommands.SHOW;
export const APPEND_LINE = OutputCommands.APPEND_LINE;

@injectable()
export class ChannelAPIContribution implements FrontendApplicationContribution {
    @inject(CommandRegistry)
    protected commands: CommandRegistry;
    @inject(OutputChannelManager)
    protected channelManager: OutputChannelManager;

    onStart?(app: FrontendApplication): MaybePromise<void> {
        this.commands.registerCommand(CREATE_CHANNEL, new CreateChannelHandler(this.channelManager));
    }
}

class CreateChannelHandler implements CommandHandler {
    channelManager: OutputChannelManager;

    constructor(channelManager: OutputChannelManager) {
        this.channelManager = channelManager;
    }

    execute(...args: any[]): OutputChannel {
        if (args.length <= 0 || args[0].name === undefined) {
            throw new Error('command CREATE_CHANNEL needs a name argument!');
        }
        return this.channelManager.getChannel(args[0].name);
    }
    isEnabled?(...args: any[]): boolean {
        return true;
    }
    isVisible?(...args: any[]): boolean {
        return true;
    }
    isToggled?(...args: any[]): boolean {
        return true;
    }
}
