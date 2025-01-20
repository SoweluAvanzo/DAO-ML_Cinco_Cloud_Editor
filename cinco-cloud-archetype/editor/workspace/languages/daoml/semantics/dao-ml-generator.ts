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

import { GeneratorHandler, GraphModel, LanguageFilesRegistry, ModelElement, RootPath, getLanguageFolder, getWorkspaceRootUri } from '@cinco-glsp/cinco-glsp-api';
import { Action, GeneratorAction } from '@cinco-glsp/cinco-glsp-common';
import { exec, ExecException } from 'child_process';
import * as path from 'path';

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
            if(GraphModel.is(model)) {
                this.generate(model);            
            } else {
                throw new Error("Generator input is no GraphModel: "+model?.id);
            }
        } catch(e) {
            this.log(e, {show: true});
        }
        return [];
    }

    override canExecute(action: GeneratorAction, ...args: unknown[]): Promise<boolean> | boolean {
        const element = this.getElement(action.modelElementId);
        return element !== undefined;
    }

    /**
     * generate files
     */
    async generate(model: GraphModel): Promise<void> {
        const sourceUri = model._sourceUri;
        const optimizedTranslation = model.getProperty('optimizedTranslation');
        const languagesFolder = getLanguageFolder();
        const execFolder = path.join(languagesFolder, 'daoml', 'generator');
        const generatorExecutionCmd = "python3 translator_cli.py -fn=translate -f=" + sourceUri + " -tt="+(optimizedTranslation ? 'optimized' : ' simple');

        const command = 'cd '+execFolder + ' && '
            + generatorExecutionCmd;

        this.log("Checking dependencies...");
        
        const dependencies = [ 'lxml', 'xmlschema', 'antlr4-python3-runtime', 'networkx', 'jinja2' ];
        for(const dep of dependencies) {
            if(!(await this.checkInstallPackages(dep))) {
                return;
            }
        }

        let code = await this.executeProcess(command, false, (error: ExecException | null, stdout: string, stderr: string) => {
            if (error) {
                this.log(`${error.message}`, {show: true, logLevel: 1});
                return;
            }
            if (stderr) {
                this.log(`${stderr}`, {show: true, logLevel: 1});
                return;
            }
            this.log(`${stdout}`, {show: true});
        });
        if(code == 1) { // not installed
            this.log("failed to execute generator!");
            return;
        }

        // copy files
        this.copyDirectory(
            path.join('daoml', 'generator', 'translated'),
            path.join('output'),
            true,
            true,
            RootPath.LANGUAGES
        );
        this.log("Output Generated!");
    }

    async checkInstallPackages(packageName: string): Promise<boolean> {
        const pip = "python3 -m pip";
        let code = await this.executeProcess(pip + " show " + packageName, false);
        if(code == 1) { // not installed
            this.log("installing missing package!")
            code = await this.executeProcess(pip + " install " + packageName, false);
            if(code == 1) {
                this.log("failed to install missing package!");
                return false;
            }
            this.log("installed missing package!");
        }
        return true;
    }

    executeProcess(command: string, logging: boolean = true, callback?: (error: ExecException | null, stdout: string, stderr: string) => void): Promise<any> {
        let lock: any;
        const childProcess = exec(command, (error, stdout, stderr) => {
            if(callback) {
                callback(error, stdout, stderr);           
            } else {
                if(logging) {
                    if (error) {
                        this.log(`Error: ${error.message}`, {show: true, logLevel: 1});
                        return;
                    }
                    if (stderr) {
                        this.log(`Stderr: ${stderr}`, {show: true, logLevel: 1});
                        return;
                    }
                    this.log(`Output: ${stdout}`, {show: true});
                }
            }
        });
        childProcess.on("spawn", (args: any) => {
            if(logging) {
                this.log("Process spawned"
                + (args ? " with args: " + args: "!"), {show: true, logLevel: 0});
            }
        });
        childProcess.on("disconnect", () => {
            if(logging) {
                this.log("Process disconneced!", {show: true, logLevel: 4})                
            }
        });
        childProcess.on("message", (message, _sendHandle) => {
            if(logging) {
                this.log(""+message.toString(), {show: true, logLevel: 4})
            }
        });
        childProcess.on("error", arg => {
            if(logging) {
                this.log("Generator had error: "
                    + arg, {show: true, logLevel: 1})
            }
        });
        childProcess.on("exit", (code, _signal) => {
            if(logging) {
                this.log("Generator-Process exited with code: "
                    + code, {show: true, logLevel: 4})
            }
        });
        childProcess.on('close', (code, _signal) => {
            if(logging) {
                this.log("Generator-Process closed with code: "
                    + code, {show: true, logLevel: 4})
            }
            lock(code);
        });
        return new Promise<any>(resolve => {
            lock = resolve;
        })        
    }

    /**
     * Describe your file content here !
     */
    getContent(model: ModelElement): string {
        return model.type + ' generation content';
    }
}
// register into app
LanguageFilesRegistry.register(DaoMLGenerator);
