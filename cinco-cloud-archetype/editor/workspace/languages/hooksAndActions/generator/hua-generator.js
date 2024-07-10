"use strict";
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
Object.defineProperty(exports, "__esModule", { value: true });
exports.HooksAndActionsExampleGenerator = void 0;
const cinco_glsp_api_1 = require("@cinco-glsp/cinco-glsp-api");
/**
 * Language Designer defined example of a Generator
 */
class HooksAndActionsExampleGenerator extends cinco_glsp_api_1.GeneratorHandler {
    constructor() {
        super(...arguments);
        this.CHANNEL_NAME = 'Flowgraph [' + this.modelState.root.id + ']';
    }
    execute(action, ...args) {
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
    canExecute(action, ...args) {
        const element = this.getElement(action.modelElementId);
        return element !== undefined;
    }
    /**
     * generate files
     */
    generate(model) {
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
    getContent(model) {
        return model.type + ' generation content';
    }
}
exports.HooksAndActionsExampleGenerator = HooksAndActionsExampleGenerator;
// register into app
cinco_glsp_api_1.LanguageFilesRegistry.register(HooksAndActionsExampleGenerator);
