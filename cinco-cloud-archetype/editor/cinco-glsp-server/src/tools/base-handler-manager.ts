/********************************************************************************
 * Copyright (c) 2022 Cinco Cloud.
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
import { APIBaseHandler, GraphModelState, LanguageFilesRegistry, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import { ManagedBaseAction } from '@cinco-glsp/cinco-glsp-common';
import { Action, ActionDispatcher, ActionHandler, Logger } from '@eclipse-glsp/server-node';
import { inject, injectable } from 'inversify';

/**
 * a base handler for the base action
 */

@injectable()
export abstract class BaseHandlerManager<A extends ManagedBaseAction, H extends APIBaseHandler> implements ActionHandler {
    @inject(Logger)
    protected readonly logger: Logger;
    @inject(GraphModelState)
    protected readonly modelState: GraphModelState;
    @inject(ActionDispatcher)
    protected readonly actionDispatcher: ActionDispatcher;

    // this needs to contain KIND of the ManagedBaseAction A
    abstract actionKinds: string[];

    // this needs to be the name of the Handler, e.g. DoubleClickHandler
    abstract baseHandlerName: string;

    // this defines, if an element has any handlerClass that is associated with this manager
    abstract hasHandlerProperty(element: ModelElement): boolean;

    // this defines if a specific handlerclass, can be applied to a given element
    abstract isApplicableHandler(element: ModelElement, handlerClassName: string): boolean;

    // checks if specific handler can be executed under the given context
    abstract handlerCanBeExecuted(handler: H, element: ModelElement, action: A, args: any): Promise<boolean> | boolean;

    // executes handler under the given context
    abstract executeHandler(handler: H, element: ModelElement, action: A, args: any): Promise<Action[]> | Action[];

    execute(action: A, ...args: unknown[]): Promise<Action[]> {
        return new Promise<Action[]>((resolve, _) => {
            const results: Action[] = [];
            this.getActiveHandlers(action, args).then(handlers => {
                if (handlers.length <= 0) {
                    resolve([]);
                }
                let leftToHandle: number = handlers.length;
                console.log(leftToHandle + ' handlers will be executed as ' + this.baseHandlerName + '...');
                handlers.forEach(handler => {
                    console.log(handler + ' handler will be executed...');
                    const element = this.modelState.index.findElement(action.modelElementId) as ModelElement;
                    try {
                        const result = this.executeHandler(handler, element, action, args);
                        if (result instanceof Promise) {
                            result.then((v: Action[]) => {
                                results.push(...v);
                                leftToHandle = leftToHandle - 1;
                                if (leftToHandle <= 0) {
                                    resolve(results);
                                }
                            });
                        } else {
                            results.push(...result);
                            leftToHandle = leftToHandle - 1;
                            if (leftToHandle <= 0) {
                                resolve(results);
                            }
                        }
                    } catch (e) {
                        console.log(`Error executing handler: ${(handler as any).name}`);
                        console.log(`${e}`);
                        leftToHandle = leftToHandle - 1;
                        if (leftToHandle <= 0) {
                            resolve(results);
                        }
                    }
                });
            });
        });
    }

    /**
     * @param action the action that would trigger the associated action
     * @param args the arguments passed along the associated action
     * @returns an Array of Handlers, that can be executed with the associated action
     */
    getActiveHandlers(action: A, ...args: unknown[]): Promise<H[]> {
        const element = this.modelState.index.findElement(action.modelElementId) as ModelElement;
        return new Promise<H[]>((resolve, reject) => {
            try {
                if (!this.hasHandlerProperty(element)) {
                    return resolve([]);
                }
            } catch (e) {
                console.log(`Error checking handlerProperties: (${element?.type + '|' + element?.id})`);
                console.log(`${e}`);
                return resolve([]);
            }
            const applicableHandlerClasses = BaseHandlerManager.getHandlerClasses(
                this.baseHandlerName,
                (handlerClassName: string): boolean => {
                    try {
                        return this.isApplicableHandler(element, handlerClassName);
                    } catch (e) {
                        console.log(`Error checking applicability of: ${handlerClassName}`);
                        console.log(`${e}`);
                        return false;
                    }
                }
            );
            let leftToHandle: number = applicableHandlerClasses.length;
            const actionHandlers: H[] = [];
            console.log('[' + leftToHandle + '] handlers will be tested for execution as a ' + this.baseHandlerName + '!');
            for (const handlerClass of applicableHandlerClasses) {
                // initialize handler
                const handler = new handlerClass(this.logger, this.modelState, this.actionDispatcher);
                // test if handler can be executed =>
                try {
                    // test if handler can be executed
                    const canExecute = this.handlerCanBeExecuted(handler, element, action, args);
                    if (canExecute instanceof Promise) {
                        // mark handler as active/executable
                        canExecute.then(value => {
                            if (value) {
                                console.log('[' + handler.name + '] can be executed as a ' + this.baseHandlerName + '!');
                                actionHandlers.push(handler);
                            }
                            leftToHandle = leftToHandle - 1;
                            if (leftToHandle <= 0) {
                                resolve(actionHandlers);
                            }
                        });
                    } else {
                        // mark handler as active/executable
                        if (canExecute) {
                            console.log('[' + handler.name + '] can be executed as a ' + this.baseHandlerName + '!');
                            actionHandlers.push(handler);
                        }
                        leftToHandle = leftToHandle - 1;
                        if (leftToHandle <= 0) {
                            resolve(actionHandlers);
                        }
                    }
                } catch (e) {
                    console.log(`Error checking executability of: ${handlerClass.name}`);
                    console.log(`${e}`);
                    leftToHandle = leftToHandle - 1;
                    if (leftToHandle <= 0) {
                        resolve(actionHandlers);
                    }
                }
            }
        });
    }

    /**
     * @returns classes of all registered language-files that are handlers for the associated class-name and fullfill the filter
     */
    static getHandlerClasses(name: string, filter?: (arg0: string) => boolean): any[] {
        const result: any[] = [];
        for (const clss of LanguageFilesRegistry.getRegistered()) {
            if (clss.__proto__.name === name && (filter === undefined || filter(clss.name))) {
                result.push(clss);
            }
        }
        return result;
    }
}
