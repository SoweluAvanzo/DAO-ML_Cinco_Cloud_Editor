/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/

import { LanguageFilesRegistry, ModelElement, ValueProvider } from '@cinco-glsp/cinco-glsp-api';
import { Action, ValueUpdateRequestAction } from '@cinco-glsp/cinco-glsp-common';
import { updateModel } from './helper/dao-ml-value-provider-helper';

export class DaoMLValueProvider extends ValueProvider {
    override CHANNEL_NAME: string | undefined = 'DAO ML';

    override updateValue(action: ValueUpdateRequestAction, ...args: unknown[]): Promise<Action[]> | Action[] {
        const currentModelElement = this.getElement(action.modelElementId);

        // Validate element exists before processing
        if (currentModelElement === undefined) {
            this.logger.warn(`Element ${action.modelElementId} not found for DAO ML value update`);
            return [];
        }

        try {
            updateModel(currentModelElement as ModelElement);
        } catch (e) {
            this.logger.error(`Error updating DAO ML value for element ${action.modelElementId}:`, e);
        }
        return [];
    }
}

LanguageFilesRegistry.register(DaoMLValueProvider);
