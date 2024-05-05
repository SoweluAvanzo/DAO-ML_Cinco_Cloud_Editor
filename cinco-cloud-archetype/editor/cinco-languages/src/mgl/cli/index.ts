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
import { MglModel } from '../../generated/ast';
import { createMglServices } from '../language/mgl-module';
import { extractAstNode } from './cli-util';
import { MGLGenerator } from './generator';
import { NodeFileSystem } from 'langium/lib/node/index';

export const loadLanguage = async (filePath: string, opts: GenerateOptions): Promise<any> => {
    const services = createMglServices(NodeFileSystem).Mgl;
    const model = await extractAstNode<MglModel>(filePath, services);
    const generatedMetaSpecification = await new MGLGenerator().generateMetaSpecification(model, filePath, services).catch(e => {
        console.log('Generation failed with error:\n' + e);
    });
    if (generatedMetaSpecification === undefined) {
        throw new Error('Generation failed!');
    }
    console.log('Successfully loaded: ' + filePath);
    return generatedMetaSpecification;
};

export interface GenerateOptions {
    destination?: string;
}
