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
 import { Container, FileCodecHandler, GraphModel, LanguageFilesRegistry} from '@cinco-glsp/cinco-glsp-api';
 
 /**
  * Language Designer defined example of a FileCodecHandler
  */
 export class ExampleFileCodecHandler extends FileCodecHandler {
    override CHANNEL_NAME: string | undefined = 'CodecTest [ CODEC ]';
     decode(content: string): GraphModel | undefined {
        this.log("Decoding model...");
        if(content == '') {
            return undefined; // triggers new creation
        }
        return JSON.parse(content);
     }
     encode(model: GraphModel): string {
        this.log("Encoding model: "+model.id);
        return JSON.stringify(model, undefined, 4);
     }
 }
 
 // register into app
 LanguageFilesRegistry.register(ExampleFileCodecHandler);
 