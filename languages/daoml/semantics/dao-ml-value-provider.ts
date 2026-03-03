/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/

import { LanguageFilesRegistry, Node, ModelElement, ValueProvider } from '@cinco-glsp/cinco-glsp-api';
import { Action, ValueUpdateRequestAction } from '@cinco-glsp/cinco-glsp-common';
import { updateValue } from './helper/dao-ml-value-provider-helper';

export class DaoMLValueProvider extends ValueProvider {
    override CHANNEL_NAME: string | undefined = 'DAO ML';

    override updateValue(action: ValueUpdateRequestAction, ...args: unknown[]): Promise<Action[]> | Action[] {
        const reason = action.reason;
        const currentModelElement: any | undefined = this.getElement(action.modelElementId) as Node;
        try {
            updateValue(currentModelElement as ModelElement, reason);
        } catch (e) {
            console.log(e);
        }
        return [];
    }
}

LanguageFilesRegistry.register(DaoMLValueProvider);
