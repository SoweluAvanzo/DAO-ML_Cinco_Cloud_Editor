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

import { HookTypes } from '@cinco-glsp/cinco-glsp-common';
import { LanguageFilesRegistry } from './language-files-registry';

export class HookRegistry {
    static _registered_hooks: Map<string, Map<HookTypes, any[]>> = new Map();
    static registerHooks(hooks: any): void {
        const modelTypeId: string = hooks.typeId;
        const hookTypes: HookTypes[] = hooks.hookTypes;
        hookTypes.forEach(hookType => {
            let hookRegistry: Map<HookTypes, any[]> | undefined = this._registered_hooks.get(modelTypeId);
            if (!hookRegistry) {
                hookRegistry = new Map();
                hookRegistry.set(hookType, [hooks]);
                this._registered_hooks.set(modelTypeId, hookRegistry);
            } else {
                let hookList: any[] | undefined = hookRegistry.get(hookType);
                if (!hookList) {
                    hookList = [hooks];
                } else {
                    (hookList as any[]).push(hooks);
                }
                hookRegistry.set(hookType, hookList);
                this._registered_hooks.set(modelTypeId, hookRegistry);
            }
        });
        LanguageFilesRegistry.register(hooks);
    }

    static getHooks(modelTypeId: string, hookType: HookTypes): any[] {
        const hookMap = this.getAllHooks(modelTypeId);
        if (hookMap) {
            const hooks = hookMap.get(hookType);
            if (hooks) {
                return hooks;
            }
        }
        return [];
    }

    static getAllHooks(modelTypeId: string): Map<string, any[]> | undefined {
        return this._registered_hooks.get(modelTypeId);
    }

}
