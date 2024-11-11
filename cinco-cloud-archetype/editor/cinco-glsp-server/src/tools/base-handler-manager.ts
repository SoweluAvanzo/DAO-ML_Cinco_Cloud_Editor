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
import { GLSP_TEMP_ID, ManagedBaseAction } from '@cinco-glsp/cinco-glsp-common';
import {
    Action,
    ActionDispatcher,
    ActionHandler,
    Logger,
    SaveModelAction,
    MessageAction,
    SeverityLevel,
    SourceModelStorage,
    ModelSubmissionHandler
} from '@eclipse-glsp/server';
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
    @inject(SourceModelStorage)
    protected sourceModelStorage: SourceModelStorage;
    @inject(ModelSubmissionHandler)
    protected submissionHandler: ModelSubmissionHandler;

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

    async execute(action: A, ...args: unknown[]): Promise<Action[]> {
        if (
            // Is not a user client?
            [GLSP_TEMP_ID].includes(this.modelState.clientId) ||
            // not the associated graphmodel
            !this.modelState.graphModel ||
            ('modelId' in action && action.modelId !== this.modelState.graphModel?.id)
        ) {
            return Promise.resolve([]);
        }

        const result: Action[] = [];
        const handlers = await this.getActiveHandlers(action, args);
        if (handlers.length <= 0) {
            return result;
        }

        console.log(handlers.length + ' handlers will be executed as ' + this.baseHandlerName + '...');
        result.push(SaveModelAction.create());
        await Promise.all(
            handlers.map(async handler => {
                console.log(handler?.constructor.name + ' handler will be executed...');
                const element = this.modelState.index.findElement(action.modelElementId) as ModelElement;
                try {
                    const executedResult = await this.executeHandler(handler, element, action, args);
                    result.push(...executedResult);
                } catch (e) {
                    console.log(`Error executing handler: ${handler?.constructor.name}`);
                    this.notify(`${handler.constructor.name} ran into errors!`, 'ERROR');
                    console.log(`${e}`);
                }
            })
        );
        return result;
    }

    /**
     * @param action the action that would trigger the associated action
     * @param args the arguments passed along the associated action
     * @returns an Array of Handlers, that can be executed with the associated action
     */
    async getActiveHandlers(action: A, ...args: unknown[]): Promise<H[]> {
        console.log('Getting handlers for element: ' + action.modelElementId);
        console.log('-> In model: ' + this.modelState?.graphModel?.id);
        const element = this.modelState.index.findElement(action.modelElementId) as ModelElement;
        if (!element) {
            console.log('Element not found in model!');
            return Promise.resolve([]);
        }
        try {
            if (!this.hasHandlerProperty(element)) {
                console.log('This element has no assigned ' + this.baseHandlerName + '!');
                return Promise.resolve([]);
            }
        } catch (e) {
            console.log(`Error checking handlerProperties: (${element?.type + '|' + element?.id})`);
            this.notify(`Error checking handlerProperties: (${element?.type + '|' + element?.id})`, 'ERROR');
            console.log(`${e}`);
            return Promise.resolve([]);
        }
        const applicableHandlerClasses = await this.getApplicableHandlers(element);
        return this.getExecutableHandlers(applicableHandlerClasses, element, action, args);
    }

    async getExecutableHandlers(applicableHandlerClasses: any[], element: ModelElement, action: A, ...args: unknown[]): Promise<H[]> {
        const actionHandlers: H[] = [];
        const promisedProcedure: Promise<void>[] = [];
        console.log('[' + applicableHandlerClasses.length + '] handlers will be tested for execution as a ' + this.baseHandlerName + '!');
        for (const handlerClass of applicableHandlerClasses) {
            try {
                // initialize handler
                const handler = new handlerClass(
                    this.logger,
                    this.modelState,
                    this.actionDispatcher,
                    this.sourceModelStorage,
                    this.submissionHandler
                );
                // test if handler can be executed
                const canExecute = this.handlerCanBeExecuted(handler, element, action, args);
                if (canExecute instanceof Promise) {
                    // mark handler as active/executable
                    promisedProcedure.push(
                        canExecute.then(value => {
                            if (value) {
                                console.log('[' + handlerClass.name + '] can be executed as a ' + this.baseHandlerName + '!');
                                actionHandlers.push(handler);
                            }
                        })
                    );
                } else {
                    // mark handler as active/executable
                    if (canExecute) {
                        console.log('[' + handlerClass.name + '] can be executed as a ' + this.baseHandlerName + '!');
                        actionHandlers.push(handler);
                    }
                }
            } catch (e) {
                console.log(`Error checking executability of: ${handlerClass.name}`);
                this.notify(`Error checking executability of: ${handlerClass.name}`, 'ERROR');
                console.log(`${e}`);
            }
        }
        await Promise.all(promisedProcedure);
        return actionHandlers;
    }

    async getApplicableHandlers(element: ModelElement): Promise<H[]> {
        const applicableHandlerClasses = await BaseHandlerManager.getHandlerClasses(
            this.baseHandlerName,
            (handlerClassName: string): boolean => {
                try {
                    return this.isApplicableHandler(element, handlerClassName);
                } catch (e) {
                    console.log(`Error checking applicability of: ${handlerClassName}`);
                    this.notify(`Error checking applicability of: ${handlerClassName}`, 'ERROR');
                    console.log(`${e}`);
                    return false;
                }
            }
        );
        if (applicableHandlerClasses.length <= 0) {
            this.notify(
                'No ' + this.baseHandlerName + ' was found! Please make sure that the annotated ' + this.baseHandlerName + ' exists!',
                'ERROR'
            );
            return Promise.resolve([]);
        }
        return applicableHandlerClasses;
    }

    /**
     * @returns classes of all registered language-files that are handlers for the associated class-name and fullfill the filter
     */
    static async getHandlerClasses(name: string, filter?: (arg0: string) => boolean): Promise<any[]> {
        const result: any[] = [];
        for (const clss of LanguageFilesRegistry.getRegisteredSync()) {
            if (clss.__proto__.name === name && (filter === undefined || filter(clss.name))) {
                result.push(clss);
            }
        }
        return result;
    }

    /**
     *
     * @param message
     * @param severity "NONE" | "INFO" | "WARNING" | "ERROR" | "FATAL" | "OK"
     * @returns
     */
    notify(message: string, severity?: SeverityLevel, details?: string, timeout?: number): void {
        const messageAction = MessageAction.create(message, {
            severity: severity ?? 'INFO',
            details: details ?? ''
        });
        this.actionDispatcher.dispatch(messageAction).catch(e => {
            console.log(e);
        });
    }
}
