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

import { Action } from '@eclipse-glsp/server-node';
import URI from '@theia/core/lib/common/uri';
import { GeneratorAction, GeneratorCreateFileOperation } from '../src/shared/protocol/generator-protocol';
import { GeneratorHandler } from '../src/tools/api/generator-handler';
import { LanguageFilesRegistry } from '../src/tools/language-files-registry';
import { ModelElement } from './model/graph-model';

export interface DidCreateNewResourceEvent {
    uri: URI;
    parent: URI;
}
/**
 * Language Designer defined example of a Generator
 */
export class ExampleGeneratorHandler extends GeneratorHandler {
    override CHANNEL_NAME: string | undefined = 'Flowgraph [' + this.modelState.root.id + ']';

    override execute(action: GeneratorAction, ...args: unknown[]): Promise<Action[]> | Action[] {
        // parse action
        const modelElementId: string = action.modelElementId;
        const target: string = action.targetFolder ?? '';
        const targetFolderUri = new URI(target);
        const element = this.modelState.index.findElement(modelElementId)! as ModelElement;
        const filesContentSMap: Map<URI, string> = this.getfileContentsMap(element, targetFolderUri);
        // const graphContent: string = this.getContent(element);

        //  logging
        const message = 'Element [' + element.type + '] generation process  started';
        this.log(message, { show: true });

        return [GeneratorCreateFileOperation.create(action.modelElementId, filesContentSMap)];
    }

    override canExecute(action: GeneratorAction, ...args: unknown[]): Promise<boolean> | boolean {
        const element = this.getElement(action.modelElementId);
        return element !== undefined;
    }
    /**
     * Set your generated Map filename-filecontent here !
     */
    getfileContentsMap(model: ModelElement, parentUri: URI): Map<URI, string> {
        const fileContentsMap = new Map<URI, string>();

        // add your different files here !
        fileContentsMap.set(parentUri.resolve('generated_flowgraph.txt'), this.getContent(model));
        fileContentsMap.set(parentUri.resolve('generated_static_file.txt'), 'static content');

        return fileContentsMap;
    }

    /**
     * Describe your file content here !
     */
    getContent(model: ModelElement): string {
        return model.type + ' generation content';
    }
}
// register into app
LanguageFilesRegistry.register(ExampleGeneratorHandler);
