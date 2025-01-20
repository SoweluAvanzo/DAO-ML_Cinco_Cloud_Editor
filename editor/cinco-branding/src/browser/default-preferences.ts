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
import { PreferenceContribution } from '@theia/core/lib/browser';

export interface PreferenceSchema {
    [name: string]: any;
    properties: {
        [name: string]: object;
    };
}

export class CincoCloudDefaultPreferences implements PreferenceContribution {
    get schema(): PreferenceSchema {
        return {
            id: 'defaultOverrides', // this is needed
            type: 'object',
            properties: {
                // FileWatcher
                'files.watcherExclude': {
                    type: 'object',
                    default: {
                        '**/node_modules/**': true,
                        '**/.git/**': true,
                        '**/dist/**': true
                    },
                    description: 'Glob patterns to exclude from the file watcher.'
                },
                'files.maxWatcherLimit': {
                    type: 'number',
                    default: 10000,
                    description: 'The maximum number of files the watcher will handle.'
                },
                // Color Theme
                'workbench.colorTheme': {
                    type: 'string',
                    default: 'dark',
                    description: 'The default theme for the Cinco Cloud IDE.'
                }
            }
        };
    }
}
