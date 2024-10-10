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

 import { GeneratorHandler, GraphModel, LanguageFilesRegistry, RootPath } from '@cinco-glsp/cinco-glsp-api';
 import { Action, GeneratorAction } from '@cinco-glsp/cinco-glsp-common';
 
 /**
  * Language Designer defined example of a Generator
  */
 export class HalloweenGenerator extends GeneratorHandler {
     override CHANNEL_NAME: string | undefined = 'Halloween [' + this.modelState.graphModel.id + ']';
 
     override async execute(action: GeneratorAction, ...args: unknown[]): Promise<Action[]> {
         // parse action
         const model = this.getElement(action.modelElementId);
         const isValid = await model.valid
         if(!isValid || !GraphModel.is(model)) {
             this.notify("Model is not valid! Please fix it, before generating.", "ERROR");
             return [];
         }
         this.log('Game generation started', { show: true });
         
         // generate
         this.generate(model);
 
         //  logging
         const message = 'Game generated!';
         this.log(message, { show: true });
 
         return [];
     }
 
     override canExecute(action: GeneratorAction, ...args: unknown[]): Promise<boolean> | boolean {
         const element = this.getElement(action.modelElementId);
         return element !== undefined;
     }
 
     /**
      * generate files
      */
     generate(model: GraphModel): void {
        const targetPath = 'halloween-game/';
        this.log('Collecting information', { show: true });
        if (!this.existsDirectory(targetPath, RootPath.WORKSPACE)) {
            this.createDirectory(targetPath, true);
        }
        this.createFile(targetPath + 'config.js', this.getContent(model));
        this.copyDirectory("halloween/src", targetPath, false, true, RootPath.LANGUAGES);

     }
 
     /**
      * Describe your file content here !
      */
     getContent(model: GraphModel): string {
        let width: string = "";
        let height: string = "";
        let player: string = "";
        const items: string[] = [];
        const enemies: string[] = [];
        const obstacles: string[] = [];
        model.getAllContainedElements().forEach(e => {
            switch(e.type) {
                case "halloween:player": {
                    player = "" +
                    `
                    {
                        maxLife: ${e.getProperty("maxLife")},
                        speed: ${e.getProperty("speed")},
                        x: ${e.position.x},
                        y: ${e.position.y}
                    }
                    `
                }
                break;
                case "halloween:level": {
                    width = ""+e.size.width;
                    height = ""+ e.size.height;
                }
                break;
                case "halloween:stump": {
                    obstacles.push("" +
                    `
                    {
                        type: "obstacle1",
                        x: ${e.position.x},
                        y: ${e.position.y}
                    }
                    `);
                }
                break;
                case "halloween:tree": {
                    obstacles.push("" +
                    `
                    {
                        type: "obstacle2",
                        x: ${e.position.x},
                        y: ${e.position.y}
                    }
                    `);
                }
                break;
                case "halloween:heart": {
                    items.push("" +
                    `
                    {
                        type: "heart",
                        heal: ${e.getProperty("heal")},
                        x: ${e.position.x},
                        y: ${e.position.y}
                    }
                    `);
                }
                break;
                case "halloween:skull": {
                    enemies.push("" +
                    `
                    {
                        type: "skull",
                        attack: ${e.getProperty("attack")},
                        speed: ${e.getProperty("speed")},
                        x: ${e.position.x},
                        y: ${e.position.y}
                    }
                    `);
                }
                break;
                case "halloween:slicer": {
                    enemies.push("" +
                    `
                    {
                        type: "slicer",
                        attack: ${e.getProperty("attack")},
                        speed: ${e.getProperty("speed")},
                        x: ${e.position.x},
                        y: ${e.position.y}
                    }
                    `);
                }
                break;
                default:
                    this.error("Generation Error: unknown type - "+e.type);
            }
        });

        return ""+
        `
        const config = {
            width: ${width},
            height: ${height},
            player: ${player},
            obstacles:[
                ${obstacles.join(',\n')}                
            ],
            items: [
                ${items.join(',\n')}    
            ],
            enemies: [
                ${enemies.join(',\n')}    
            ],
        
        }
        `
     }
 }
 
 // register into app
 LanguageFilesRegistry.register(HalloweenGenerator);
 