/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/
import {
    FileCodecHandler,
    GraphModel,
    LanguageFilesRegistry
} from '@cinco-glsp/cinco-glsp-api';
import { decodeSwitch, encodeSwitch } from './helper/dao-ml-codec-helper';

/**
 * Language Designer defined example of a FileCodecHandler
 */
export class DaoMLFileCodecHandler extends FileCodecHandler {
    override CHANNEL_NAME: string | undefined = 'DaoML [ CODEC ]';

    override decode(content: string): GraphModel | undefined {
        if (content === '') {
            return undefined; // triggers new creation
        }

        const graphModel = new GraphModel();
        let result: GraphModel;
        try {
            result = decodeSwitch('DAO-ML_diagram', content, graphModel,
                { edge_references: [], containment_references: [], log: this.log }) as GraphModel;
        } catch (e: any) {
            throw new Error('> Decoding Error >' + e.stack);
        }
        return result;
    }

    override encode(model: GraphModel): string {
        let result: string | undefined;
        try {
            result = encodeSwitch(model, model);
            if (!result) {
                throw new Error('Could not encode model!');
            }
        } catch (e: any) {
            this.log(e);
            throw new Error('> Encoding Error > ' + e.stack);
        }
        return result;
    }
}

// register into app
LanguageFilesRegistry.register(DaoMLFileCodecHandler);
