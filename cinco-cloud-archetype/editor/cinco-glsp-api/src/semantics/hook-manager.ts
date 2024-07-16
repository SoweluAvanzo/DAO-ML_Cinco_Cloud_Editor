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
    hasHooks,
    DoubleClickArgument,
    getHooksOfType
} from '@cinco-glsp/cinco-glsp-common';
import { ActionDispatcher, Logger, MessageAction, SeverityLevel } from '@eclipse-glsp/server';
import {
    AbstractHook,
    AttributeHook,
    ModelElementHook,
    NodeElementHook,
    EdgeElementHook,
    GraphicalElementHook,
    AbstractNodeHook,
    AbstractEdgeHook,
    AbstractUserDefinedTypeHook,
    AbstractGraphModelHook
} from '../api/hook-handler';
import { LanguageFilesRegistry } from './language-files-registry';
import { ModelElement, Edge, Node, ModelElementContainer } from '../model/graph-model';
import { GraphModelState } from '../model/graph-model-state';

export class HookManager {
    static executeHook(
        parameters: OperationArgument,
        type: HookTypes,
        modelState: GraphModelState,
        logger: Logger,
        actionDispatcher: ActionDispatcher
    ): boolean {
        let elementTypeId = '';
        if (parameters.kind === 'Create') {
            elementTypeId = (parameters as CreateArgument).elementTypeId;
        } else {
            if (parameters.modelElementId !== '<NONE>') {
                const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                if (modelElement) {
                    elementTypeId = modelElement.type;
                } else {
                    return false;
                }
            }
        }
        // load hook class
        const hookClassNames = this.getSuitableClassNames(elementTypeId, type);
        const hookResults: boolean[] = [];
        // execute all hooks
        for (const hookClassName of hookClassNames) {
            const hookClass = this.loadHookClasses(hookClassName, elementTypeId, type);
            try {
                const hook = new hookClass(logger, modelState, actionDispatcher);
                const result = this.dispatchHook(hook, parameters, type, modelState, logger, actionDispatcher);
                hookResults.push(result);
            } catch (error: any) {
                logger.error(error);
                const errorMsg =
                    'No Hook for Element "' + elementTypeId + '" with Class "' + hookClassName + '" and hook type : "' + type + '" found.';
                this.notify(actionDispatcher, errorMsg, 'ERROR');
                hookResults.push(false);
            }
        }
        if (hookClassNames.length <= 0) {
            return true;
        }
        return hookResults.filter(v => !v).length <= 0; // no false was returned => true
    }

    private static getSuitableClassNames(elementTypeId: string, hookType: HookTypes): string[] {
        if (hasHooks(elementTypeId)) {
            return getHooksOfType(elementTypeId, hookType)
                .map((value: string[]) => value[0])
                .flat();
        }
        return [];
    }

    private static loadHookClasses(hookClassName: string, modelTypeId: string, hookType: HookTypes): any {
        const hooks = LanguageFilesRegistry.getRegistered().filter((hook: any) => hook.name === hookClassName);
        return hooks.length > 0 ? hooks[0] : undefined;
    }

    private static dispatchHook(
        hook: AbstractHook,
        parameters: OperationArgument,
        type: HookTypes,
        modelState: GraphModelState,
        logger: Logger,
        actionDispatcher: ActionDispatcher
    ): boolean {
        try {
            switch (type) {
                case HookTypes.CAN_CHANGE_ATTRIBUTE: {
                    if (!AttributeHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                    }
                    const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canChangeAttributeHook(hook, parameters as AttributeChangeArgument, modelElement);
                }
                case HookTypes.PRE_ATTRIBUTE_CHANGE:
                    {
                        if (!AttributeHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.preAttributeChangeHook(hook, parameters as AttributeChangeArgument, modelElement);
                    }
                    break;
                case HookTypes.POST_ATTRIBUTE_CHANGE:
                    {
                        if (!AttributeHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postAttributeChangeHook(hook, parameters as AttributeChangeArgument, modelElement);
                    }
                    break;
                case HookTypes.CAN_CREATE: {
                    if (!ModelElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                    }
                    return this.canCreateHook(hook, parameters as CreateArgument);
                }
                case HookTypes.PRE_CREATE:
                    {
                        if (!ModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        this.preCreateHook(hook, parameters as CreateArgument, modelState);
                    }
                    break;
                case HookTypes.POST_CREATE:
                    {
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        if (!ModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        this.postCreateHook(hook, modelElement);
                    }
                    break;
                case HookTypes.CAN_DELETE:
                    {
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        if (!ModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        this.canDeleteHook(hook, modelElement);
                    }
                    break;
                case HookTypes.PRE_DELETE:
                    {
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        if (!ModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        this.preDeleteHook(hook, modelElement);
                    }
                    break;
                case HookTypes.POST_DELETE:
                    {
                        if (!ModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = (parameters as DeleteArgument).deleted;
                        if (!modelElement && ModelElement.is(modelElement)) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postDeleteHook(hook, modelElement);
                    }
                    break;
                case HookTypes.CAN_MOVE: {
                    if (!NodeElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                    }
                    const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canMoveHook(hook, parameters as MoveArgument, modelElement);
                }
                case HookTypes.PRE_MOVE:
                    {
                        if (!NodeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.preMoveHook(hook, parameters as MoveArgument, modelElement);
                    }
                    break;
                case HookTypes.POST_MOVE:
                    {
                        if (!NodeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postMoveHook(hook, parameters as MoveArgument, modelElement);
                    }
                    break;
                case HookTypes.CAN_RESIZE: {
                    if (!NodeElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                    }
                    const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canResizeHook(hook, parameters as ResizeArgument, modelElement);
                }
                case HookTypes.PRE_RESIZE:
                    {
                        if (!NodeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.preResizeHook(hook, parameters as ResizeArgument, modelElement);
                    }
                    break;
                case HookTypes.POST_RESIZE:
                    {
                        if (!NodeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postResizeHook(hook, parameters as ResizeArgument, modelElement);
                    }
                    break;
                case HookTypes.CAN_RECONNECT: {
                    if (!EdgeElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                    }
                    const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canReconnectHook(hook, parameters as ReconnectArgument, modelElement);
                }
                case HookTypes.PRE_RECONNECT:
                    {
                        if (!EdgeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.preReconnectHook(hook, parameters as ReconnectArgument, modelElement);
                    }
                    break;
                case HookTypes.POST_RECONNECT:
                    {
                        if (!EdgeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postReconnectHook(hook, parameters as ReconnectArgument, modelElement);
                    }
                    break;
                case HookTypes.CAN_SELECT: {
                    if (!GraphicalElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                    }
                    const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canSelectHook(hook, parameters as SelectArgument, modelElement);
                }
                case HookTypes.POST_SELECT:
                    {
                        if (!GraphicalElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postSelectHook(hook, parameters as SelectArgument, modelElement);
                    }
                    break;
                case HookTypes.CAN_DOUBLE_CLICK: {
                    if (!GraphicalElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                    }
                    const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canDoubleClickHook(hook, parameters as DoubleClickArgument, modelElement);
                }
                case HookTypes.POST_DOUBLE_CLICK:
                    {
                        if (!GraphicalElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postDoubleClickHook(hook, parameters as DoubleClickArgument, modelElement);
                    }
                    break;
            }
        } catch (e: any) {
            logger.error(e);
            this.notify(actionDispatcher, e.message, 'ERROR');
            return false;
        }
        return true;
    }

    /**
     * Attributes
     */

    private static canChangeAttributeHook(
        hook: AttributeHook<ModelElement>,
        parameter: AttributeChangeArgument,
        modelElement: ModelElement
    ): boolean {
        return !hook.canChangeAttribute || hook.canChangeAttribute(modelElement, parameter.operation);
    }

    private static preAttributeChangeHook(
        hook: AttributeHook<ModelElement>,
        parameters: AttributeChangeArgument,
        modelElement: ModelElement
    ): void {
        if (hook.preAttributeChange) {
            hook.preAttributeChange(modelElement, parameters.operation);
        }
    }

    private static postAttributeChangeHook(
        hook: AttributeHook<ModelElement>,
        parameters: AttributeChangeArgument,
        modelElement: ModelElement
    ): void {
        if (hook.postAttributeChange) {
            hook.postAttributeChange(modelElement, parameters.operation.name, parameters.oldValue);
        }
    }

    /**
     * Create
     */

    private static canCreateHook(hook: ModelElementHook<ModelElement>, parameters: CreateArgument): boolean {
        return !hook.canCreate || hook.canCreate(parameters.operation);
    }

    private static preCreateHook(hook: ModelElementHook<any>, parameters: CreateArgument, modelState: GraphModelState): void {
        if (hook.preCreate !== undefined) {
            switch (parameters.elementKind) {
                case 'Node':
                    {
                        const container = modelState.index.findModelElement(parameters.containerElementId);
                        if (hook instanceof AbstractNodeHook && ModelElementContainer.is(container)) {
                            hook.preCreate(container, parameters.location);
                        } else {
                            throw Error('Can not PreCreate node.');
                        }
                    }
                    break;
                case 'Edge':
                    {
                        const source = modelState.index.findNode(parameters.sourceElementId);
                        const target = modelState.index.findNode(parameters.targetElementId);
                        if (hook instanceof AbstractEdgeHook && source && target) {
                            hook.preCreate(source, target);
                        }
                    }
                    break;
                case 'UserDefinedType':
                    {
                        const args = parameters.args;
                        if (hook instanceof AbstractUserDefinedTypeHook) {
                            hook.preCreate(args);
                        }
                    }
                    break;
                case 'GraphModel': {
                    const path = parameters.path;
                    if (hook instanceof AbstractGraphModelHook) {
                        hook.preCreate(path);
                    }
                }
            }
        }
        return;
    }

    private static postCreateHook(hook: ModelElementHook<ModelElement>, modelElement: ModelElement): void {
        if (hook.postCreate) {
            hook.postCreate(modelElement);
        }
    }

    private static canDeleteHook(hook: ModelElementHook<ModelElement>, modelElement: ModelElement): boolean {
        return !hook.canDelete || hook.canDelete(modelElement);
    }

    private static preDeleteHook(hook: ModelElementHook<ModelElement>, modelElement: ModelElement): void {
        if (hook.preDelete) {
            hook.preDelete(modelElement);
        }
    }

    private static postDeleteHook(hook: ModelElementHook<ModelElement>, modelElement: ModelElement): void {
        if (hook.postDelete) {
            hook.postDelete(modelElement);
        }
    }

    private static canReconnectHook(hook: AbstractEdgeHook, parameters: ReconnectArgument, modelElement: ModelElement): boolean {
        const newSource = modelElement.index.findNode(parameters.sourceId);
        const newTarget = modelElement.index.findNode(parameters.targetId);
        if (Edge.is(modelElement) && newSource && newTarget) {
            return !hook.canChangeAttribute || hook.canReconnect(modelElement, newSource, newTarget);
        } else {
            throw new Error('Could not execute canReconnectHook.');
        }
    }

    private static preReconnectHook(hook: AbstractEdgeHook, parameters: ReconnectArgument, modelElement: ModelElement): void {
        const newSource = modelElement.index.findNode(parameters.sourceId);
        const newTarget = modelElement.index.findNode(parameters.targetId);
        if (hook.preReconnect && Edge.is(modelElement) && newSource && newTarget) {
            hook.preReconnect(modelElement, newSource, newTarget);
        } else {
            throw new Error('Could execute preReconnectHook.');
        }
    }

    private static postReconnectHook(hook: AbstractEdgeHook, parameters: ReconnectArgument, modelElement: ModelElement): void {
        const oldSource = modelElement.index.findNode(parameters.sourceId);
        const oldTarget = modelElement.index.findNode(parameters.targetId);
        if (hook.postReconnect && Edge.is(modelElement) && oldSource && oldTarget) {
            hook.postReconnect(modelElement, oldSource, oldTarget);
        } else {
            throw new Error('Could execute postReconnectHook.');
        }
    }

    private static canMoveHook(hook: AbstractNodeHook, parameters: MoveArgument, modelElement: ModelElement): boolean {
        return !hook.canMove || (Node.is(modelElement) && hook.canMove(modelElement, parameters.newPosition));
    }

    private static preMoveHook(hook: AbstractNodeHook, parameters: MoveArgument, modelElement: ModelElement): void {
        if (hook.preMove && Node.is(modelElement)) {
            hook.preMove(modelElement, parameters.newPosition);
        }
    }

    private static postMoveHook(hook: AbstractNodeHook, parameters: MoveArgument, modelElement: ModelElement): void {
        if (hook.postMove && Node.is(modelElement)) {
            hook.postMove(modelElement, parameters.oldPosition);
        }
    }

    private static canResizeHook(hook: AbstractNodeHook, parameters: ResizeArgument, modelElement: ModelElement | undefined): boolean {
        return !hook.canChangeAttribute || (Node.is(modelElement) && hook.canResize(modelElement, parameters.newSize));
    }

    private static preResizeHook(hook: AbstractNodeHook, parameters: ResizeArgument, modelElement: ModelElement | undefined): void {
        if (hook.preResize && Node.is(modelElement)) {
            hook.preResize(modelElement, parameters.newSize);
        }
    }

    private static postResizeHook(hook: AbstractNodeHook, parameters: ResizeArgument, modelElement: ModelElement | undefined): void {
        if (hook.postResize && Node.is(modelElement)) {
            hook.postResize(modelElement, parameters.oldSize);
        }
    }

    private static canSelectHook(
        hook: GraphicalElementHook<any>,
        parameters: SelectArgument,
        modelElement: ModelElement | undefined
    ): boolean {
        return !hook.canSelect || hook.canSelect(modelElement);
    }

    private static postSelectHook(
        hook: GraphicalElementHook<any>,
        parameters: SelectArgument,
        modelElement: ModelElement | undefined
    ): void {
        if (hook.postSelect) {
            hook.postSelect(modelElement);
        }
    }

    private static canDoubleClickHook(
        hook: GraphicalElementHook<any>,
        parameters: DoubleClickArgument,
        modelElement: ModelElement | undefined
    ): boolean {
        return !hook.canDoubleClick || hook.canDoubleClick(modelElement);
    }

    private static postDoubleClickHook(
        hook: GraphicalElementHook<any>,
        parameters: DoubleClickArgument,
        modelElement: ModelElement | undefined
    ): void {
        if (hook.postDoubleClick) {
            hook.postDoubleClick(modelElement);
        }
    }

    /**
     *
     * @param message
     * @param severity "NONE" | "INFO" | "WARNING" | "ERROR" | "FATAL" | "OK"
     * @returns
     */
    static notify(actionDispatcher: ActionDispatcher, message: string, severity?: SeverityLevel, details?: string, timeout?: number): void {
        const messageAction = MessageAction.create(message, {
            severity: severity ?? 'INFO',
            details: details ?? ''
        });
        actionDispatcher.dispatch(messageAction);
    }
}
