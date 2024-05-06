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

import { AbstractHooks, ModelElement, LanguageFilesRegistry } from '@cinco-glsp/cinco-glsp-api';
import {
    OperationArgument,
    CreateArgument,
    DeleteArgument,
    AttributeChangeArgument,
    ReconnectArgument,
    HookTypes,
    MoveArgument,
    ResizeArgument,
    SelectArgument,
    getHooks,
    hasHooks,
    getAllHooks
} from '@cinco-glsp/cinco-glsp-common';
import { BaseHandlerManager } from './base-handler-manager';
import { Action } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
let HOOK_MANAGER_INSTANCE: any;
@injectable()
export class HookManager extends BaseHandlerManager<OperationArgument, AbstractHooks> {
    override actionKinds: string[] = [AbstractHooks.hookName];
    override baseHandlerName = 'AbstractHook';
    readonly baseHandlerNames: string[] = ['AbstractNodeHooks', 'AbstractEdgeHooks', 'AbstractGraphModelHooks'];

    constructor() {
        super();
        HOOK_MANAGER_INSTANCE = this;
    }

    static getInstance(): HookManager {
        if (!HOOK_MANAGER_INSTANCE) {
            throw Error('HookManager Not Initialized, yet.');
        }
        return HOOK_MANAGER_INSTANCE as HookManager;
    }

    override hasHandlerProperty(element: ModelElement): boolean {
        return hasHooks(element.type);
    }

    override isApplicableHandler(element: ModelElement, handlerClassName: string): boolean {
        return (
            getAllHooks(element.type).filter(
                // In the value set of annotations, there exists a value with the handlerClassName
                (a: string[]) => a.indexOf(handlerClassName) >= 0
            ).length > 0
        );
    }

    override handlerCanBeExecuted(
        handler: AbstractHooks,
        element: ModelElement,
        action: OperationArgument,
        args: any
    ): boolean | Promise<boolean> {
        return false;
    }
    override executeHandler(
        handler: AbstractHooks,
        element: ModelElement,
        action: OperationArgument,
        args: any
    ): Action[] | Promise<Action[]> {
        return [];
    }

    private getSuitableClassNames(elementTypeId: string, hookType: HookTypes): string[] {
        if (hasHooks(elementTypeId)) {
            return getHooks(elementTypeId, hookType)
                .map((value: string[]) => value[0])
                .flat();
        }
        return [];
    }

    executeHook(parameters: OperationArgument, type: HookTypes): boolean {
        let elementTypeId = '';
        if (parameters.kind === 'Create') {
            elementTypeId = (parameters as CreateArgument).elementTypeId;
        } else {
            if (parameters.modelElementId !== '<NONE>') {
                const modelElement = this.getModelElement(parameters.modelElementId);
                if (modelElement) {
                    elementTypeId = modelElement.type;
                } else {
                    return false;
                }
            }
        }
        const hookClassNames = this.getSuitableClassNames(elementTypeId, type);

        for (const hookClassName of hookClassNames) {
            const hookClass = this.loadHookClasses(hookClassName, elementTypeId, type);
            try {
                const hk: AbstractHooks = new hookClass(this.logger, this.modelState, this.actionDispatcher);
                if (!this.dispatchHook(hk, parameters, type)) {
                    return false;
                }
            } catch (error: any) {
                this.logger.error(error);
                const errorMsg =
                    'No Hook for Element "' + elementTypeId + '" with Class "' + hookClassName + '" and hook type : "' + type + '" found.';
                this.notify(errorMsg, 'ERROR');
                return false;
            }
        }
        return true;
    }

    private loadHookClasses(hookClassName: string, modelTypeId: string, hookType: HookTypes): any {
        return LanguageFilesRegistry.getRegisteredHooks(modelTypeId, hookType).filter((hook: any) => hook.name === hookClassName)[0];
    }

    private dispatchHook(hk: AbstractHooks, parameters: OperationArgument, type: HookTypes): boolean {
        try {
            switch (type) {
                case HookTypes.CAN_CHANGE_ATTRIBUTE:
                    return this.canChangeAttributeHook(hk, parameters as AttributeChangeArgument);
                case HookTypes.PRE_ATTRIBUTE_CHANGE:
                    this.preAttributeChangeHook(hk, parameters as AttributeChangeArgument);
                    break;
                case HookTypes.POST_ATTRIBUTE_CHANGE:
                    this.postAttributeChangeHook(hk, parameters as AttributeChangeArgument);
                    break;
                case HookTypes.CAN_CREATE:
                    return this.canCreateHook(hk, parameters as CreateArgument);
                case HookTypes.PRE_CREATE:
                    this.preCreateHook(hk, parameters as CreateArgument);
                    break;
                case HookTypes.POST_CREATE:
                    this.postCreateHook(hk, parameters as CreateArgument);
                    break;
                case HookTypes.CAN_DOUBLE_CLICK:
                    throw new Error('Not Implemented, Use DoubleClick Action');
                case HookTypes.POST_DOUBLE_CLICK:
                    throw new Error('Not Implemented, Use DoubleClick Action');
                case HookTypes.CAN_DELETE:
                    return this.canDeleteHook(hk, parameters as DeleteArgument);
                case HookTypes.PRE_DELETE:
                    this.preDeleteHook(hk, parameters as DeleteArgument);
                    break;
                case HookTypes.POST_DELETE:
                    this.postDeleteHook(hk, parameters as DeleteArgument);
                    break;
                case HookTypes.CAN_MOVE:
                    return this.canMoveHook(hk, parameters as MoveArgument);
                case HookTypes.PRE_MOVE:
                    this.preMoveHook(hk, parameters as MoveArgument);
                    break;
                case HookTypes.POST_MOVE:
                    this.postMoveHook(hk, parameters as MoveArgument);
                    break;
                case HookTypes.CAN_RESIZE:
                    return this.canResizeHook(hk, parameters as ResizeArgument);
                case HookTypes.PRE_RESIZE:
                    this.preResizeHook(hk, parameters as ResizeArgument);
                    break;
                case HookTypes.POST_RESIZE:
                    this.postResizeHook(hk, parameters as ResizeArgument);
                    break;
                case HookTypes.CAN_SELECT:
                    return this.canSelectHook(hk, parameters as SelectArgument);
                case HookTypes.POST_SELECT:
                    this.postSelectHook(hk, parameters as SelectArgument);
                    break;
                case HookTypes.CAN_RECONNECT:
                    return this.canReconnectHook(hk, parameters as ReconnectArgument);
                case HookTypes.PRE_RECONNECT:
                    this.preReconnectHook(hk, parameters as ReconnectArgument);
                    break;
                case HookTypes.POST_RECONNECT:
                    this.postReconnectHook(hk, parameters as ReconnectArgument);
            }
        } catch (e) {
            return false;
        }
        return true;
    }

    private preCreateHook(hk: any, parameters: CreateArgument): void {
        if (hk.preCreate !== undefined) {
            switch (parameters.elementKind) {
                case 'Node':
                    {
                        const container = this.getModelElement(parameters.containerElementId);
                        if (container) {
                            hk.preCreate(container, parameters.location);
                        }
                    }
                    break;
                case 'Edge':
                    hk.preCreate(parameters.sourceElementId, parameters.targetElementId);
                    break;
                case 'GraphModel':
                    throw Error('Can not PreCreate for GraphModel');
            }
        }
        return;
    }

    private postCreateHook(hk: any, parameters: CreateArgument): void {
        if (hk.postCreate !== undefined) {
            const modelElement = parameters.modelElement as ModelElement;
            if (modelElement) {
                hk.postCreate(modelElement);
            }
        }
    }

    private canCreateHook(hk: any, parameters: CreateArgument): boolean {
        return !hk.canCreate || hk.canCreate(parameters.operation);
    }

    private canDeleteHook(hk: any, parameters: DeleteArgument): boolean {
        const modelElement = this.getModelElement(parameters.modelElementId);
        return modelElement && (!hk.canDelete || hk.canDelete(modelElement));
    }

    private preDeleteHook(hk: any, parameters: DeleteArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (modelElement && hk.preDelete) {
            hk.preDelete(modelElement);
        }
    }

    private postDeleteHook(hk: any, parameters: DeleteArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (modelElement && hk.postDelete) {
            hk.postDelete(modelElement);
        }
    }

    private canChangeAttributeHook(hk: any, parameter: AttributeChangeArgument): boolean {
        return !hk.canChangeAttribute || hk.canChangeAttribute(parameter.operation);
    }

    private preAttributeChangeHook(hk: any, parameters: AttributeChangeArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (modelElement && hk.preAttributeChange) {
            hk.preAttributeChange(modelElement);
        }
    }

    private postAttributeChangeHook(hk: any, parameters: AttributeChangeArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (modelElement && hk.postAttributeChange) {
            hk.postAttributeChange(modelElement, parameters.operation.name, parameters.oldValue);
        }
    }

    private canReconnectHook(hk: any, parameters: ReconnectArgument): boolean {
        return !hk.canChangeAttribute || hk.canReconnect(parameters.operation);
    }

    private preReconnectHook(hk: any, parameters: ReconnectArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (modelElement && hk.preReconnect) {
            hk.preReconnect(modelElement);
        }
    }

    private postReconnectHook(hk: any, parameters: ReconnectArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (modelElement && hk.postReconnect) {
            hk.postReconnect(modelElement);
        }
    }

    private canMoveHook(hk: any, parameters: MoveArgument): boolean {
        const modelElement = this.getModelElement(parameters.modelElementId);
        return modelElement && (!hk.canMove || hk.canMove(modelElement, parameters.newPosition));
    }

    private preMoveHook(hk: any, parameters: MoveArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (modelElement && hk.preMove) {
            hk.preMove(modelElement, parameters.newPosition);
        }
    }

    private postMoveHook(hk: any, parameters: MoveArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (modelElement && hk.postMove) {
            hk.postMove(modelElement, parameters.oldPosition);
        }
    }

    private canResizeHook(hk: any, parameters: ResizeArgument): boolean {
        const modelElement = this.getModelElement(parameters.modelElementId);
        return modelElement && (!hk.canChangeAttribute || hk.canResize(modelElement, parameters.newSize));
    }

    private preResizeHook(hk: any, parameters: ResizeArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (modelElement && hk.preResize) {
            hk.preResize(modelElement, parameters.newSize);
        }
    }

    private postResizeHook(hk: any, parameters: ResizeArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (modelElement && hk.postResize) {
            hk.postResize(modelElement, parameters.oldSize);
        }
    }

    private canSelectHook(hk: any, parameters: SelectArgument): boolean {
        const modelElement = this.getModelElement(parameters.modelElementId);
        return modelElement && (!hk.canSelect || hk.canSelect(modelElement));
    }
    private postSelectHook(hk: any, parameters: SelectArgument): void {
        const modelElement = this.getModelElement(parameters.modelElementId);
        if (hk.postSelect) {
            hk.postSelect(modelElement);
        }
    }

    private getModelElement(modelElementId: string): ModelElement | undefined {
        return this.modelState.index.findModelElement(modelElementId);
    }
}
