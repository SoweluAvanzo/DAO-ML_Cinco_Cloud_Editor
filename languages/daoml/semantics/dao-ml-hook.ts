/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/

import { LanguageFilesRegistry, AbstractGraphModelHook, GraphModel } from '@cinco-glsp/cinco-glsp-api';
import { updateModel } from './helper/dao-ml-value-provider-helper';

export class DaoMLHook extends AbstractGraphModelHook {
    override CHANNEL_NAME: string | undefined = 'DaoMLHook';

    override postContentChange(graphModel: GraphModel): void {
        // Whenever the content of the graph model changes,
        // we recursively update all elements to ensure they are
        // properly sized and positioned based on their properties and layout.
        // This is important when elements are added/removed to maintain visual consistency.
        this.updateAllElements(graphModel);
    }

    private updateAllElements(element: any): void {
        // Recursively process all containments FIRST (bottom-up)
        // This ensures children are sized before their parents
        if (element.containments && Array.isArray(element.containments)) {
            element.containments.forEach((child: any) => {
                this.updateAllElements(child);
            });
        }

        // Process the current element
        // Cascade logic in the helper will update parents automatically
        updateModel(element);
    }
}

LanguageFilesRegistry.register(DaoMLHook);
