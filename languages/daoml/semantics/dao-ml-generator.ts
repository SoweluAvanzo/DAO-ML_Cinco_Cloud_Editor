/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/

import {
    GeneratorHandler, GraphModel, LanguageFilesRegistry
} from '@cinco-glsp/cinco-glsp-api';
import { Action, GeneratorAction } from '@cinco-glsp/cinco-glsp-common';
import { generate } from './helper/dao-ml-generator-helper';

/**
 * Language Designer defined example of a Generator
 */
export class DaoMLGenerator extends GeneratorHandler {
    override CHANNEL_NAME: string | undefined = 'DaoML Generator';

    override execute(action: GeneratorAction, ...args: unknown[]): Promise<Action[]> | Action[] {
        // parse action
        const model = this.getElement(action.modelElementId);
        //  logging
        const message = 'Generation process starting...';
        this.log(message, { show: true });
        // generate
        try {
            if (GraphModel.is(model)) {
                generate(model, this.log, this.copyDirectory);
            } else {
                throw new Error('Generator input is no GraphModel: ' + model?.id);
            }
        } catch (e: any) {
            this.log(e, { show: true });
        }
        return [];
    }

    override canExecute(action: GeneratorAction, ...args: unknown[]): Promise<boolean> | boolean {
        const element = this.getElement(action.modelElementId);
        return element !== undefined;
    }

}
// register into app
LanguageFilesRegistry.register(DaoMLGenerator);
