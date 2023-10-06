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

import { GeneratorHandler, LanguageFilesRegistry, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import { GeneratorAction } from '@cinco-glsp/cinco-glsp-common';
import { Action } from '@eclipse-glsp/server-node';

/**
 * Language Designer defined example of a Generator
 */
export class ExampleGeneratorHandler extends GeneratorHandler {
    override CHANNEL_NAME: string | undefined = 'Flowgraph [' + this.modelState.root.id + ']';

    override execute(action: GeneratorAction, ...args: unknown[]): Promise<Action[]> | Action[] {
        // parse action
        const model = this.getElement(action.modelElementId);

        // generate
        this.generate(model);

        //  logging
        const message = 'Element [' + model.type + '] generation process started';
        this.log(message, { show: true });

        // const target: string = action.targetFolder ?? '';
        // const targetFolderUri = new URI(target);
        // old action based method: const filesContentSMap: Map<URI, string> = this.getfileContentsMap(model, targetFolderUri);
        // old action based method: return [GeneratorCreateFileOperation.create(action.modelElementId, filesContentSMap)];
        return [];
    }

    override canExecute(action: GeneratorAction, ...args: unknown[]): Promise<boolean> | boolean {
        const element = this.getElement(action.modelElementId);
        return element !== undefined;
    }

    /**
     * generate files
     */
    generate(model: ModelElement): void {
        this.createFile('generated_flowgraph.txt', this.getContent(model));
        this.createFile('generated_static_file.txt', 'static content');
        const writtenFile = this.readFile('generated_static_file.txt');
        console.log('WrittenFile: ' + writtenFile);
    }

    /**
     * old action based method:
     *
     * Set your generated Map filename-filecontent here !
     *
    getfileContentsMap(model: ModelElement, parentUri: URI): Map<URI, string> {
        const fileContentsMap = new Map<URI, string>();

        // add your different files here !
        fileContentsMap.set(parentUri.resolve('generated_flowgraph.txt'), this.getContent(model));
        fileContentsMap.set(parentUri.resolve('generated_static_file.txt'), 'static content');

        return fileContentsMap;
    }
    */

    /**
     * Describe your file content here !
     */
    getContent(model: ModelElement): string {
        return model.type + ' generation content';
    }
}
// register into app
LanguageFilesRegistry.register(ExampleGeneratorHandler);
