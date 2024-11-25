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

import {
    OperationArgument,
    CreateArgument,
    DeleteArgument,
    AttributeChangeArgument,
    ReconnectArgument,
    HookType,
    MoveArgument,
    ResizeArgument,
    SelectArgument,
    hasHooks,
    DoubleClickArgument,
    getHooksOfType,
    SaveModelFileArgument,
    mapCell,
    Cell,
    LayoutArgument
} from '@cinco-glsp/cinco-glsp-common';
import { ActionDispatcher, hasStringProp, MessageAction, SeverityLevel } from '@eclipse-glsp/server';
import {
    AbstractHook,
    AttributeHook,
    ModelElementHook,
    NodeElementHook,
    EdgeElementHook,
    GraphicalElementHook,
    AbstractNodeHook,
    AbstractEdgeHook,
    ModelFileHook,
    GraphModelElementHook
} from '../api/handler/hook-handler';
import { LanguageFilesRegistry } from './language-files-registry';
import { ModelElement, Edge, Node, ModelElementContainer, GraphModel } from '../model/graph-model';
import { GraphModelState } from '../model/graph-model-state';
import { APIBaseHandler } from '../api/handler/api-base-handler';
import { ResizeBounds } from '../api/types/resize-bounds';
import { ContextBundle } from '../api/types/context-bundle';

export class HookManager {
    static executeHook(parameters: OperationArgument, type: HookType, contextBundle: ContextBundle): boolean {
        let elementTypeId = '';
        if (hasStringProp(parameters, 'elementTypeId')) {
            elementTypeId = (parameters as any).elementTypeId;
        } else {
            if (parameters.modelElementId !== '<NONE>') {
                const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
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
            const hookClass = this.loadHookClasses(hookClassName);
            try {
                const hook = new hookClass(contextBundle) as APIBaseHandler;
                const result = this.dispatchHook(hook, parameters, type, contextBundle);
                hookResults.push(result);
            } catch (error: any) {
                contextBundle.logger.error(error);
                const errorMsg =
                    'No Hook for Element "' + elementTypeId + '" with Class "' + hookClassName + '" and hook type : "' + type + '" found.';
                this.notify(contextBundle.actionDispatcher, errorMsg, 'ERROR');
                hookResults.push(false);
            }
        }
        if (hookClassNames.length <= 0) {
            return true;
        }
        return hookResults.filter(v => !v).length <= 0; // no false was returned => true
    }

    private static getSuitableClassNames(elementTypeId: string, hookType: HookType): string[] {
        if (hasHooks(elementTypeId)) {
            return getHooksOfType(elementTypeId, hookType)
                .map((value: string[]) => value[0])
                .flat();
        }
        return [];
    }

    private static loadHookClasses(hookClassName: string): any {
        const hooks = LanguageFilesRegistry.getRegisteredSync().filter((hook: any) => hook.name === hookClassName);
        return hooks.length > 0 ? hooks[0] : undefined;
    }

    private static dispatchHook(hook: AbstractHook, parameters: OperationArgument, type: HookType, contextBundle: ContextBundle): boolean {
        try {
            switch (type) {
                case HookType.CAN_ATTRIBUTE_CHANGE: {
                    if (!AttributeHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                    }
                    const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canAttributeChangeHook(hook, parameters as AttributeChangeArgument, modelElement);
                }
                case HookType.PRE_ATTRIBUTE_CHANGE:
                    {
                        if (!AttributeHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.preAttributeChangeHook(hook, parameters as AttributeChangeArgument, modelElement);
                    }
                    break;
                case HookType.POST_ATTRIBUTE_CHANGE:
                    {
                        if (!AttributeHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an AttributeHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postAttributeChangeHook(hook, parameters as AttributeChangeArgument, modelElement);
                    }
                    break;
                case HookType.CAN_CREATE: {
                    if (!ModelElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelElementHook.`);
                    }
                    return this.canCreateHook(hook, parameters as CreateArgument, contextBundle.modelState);
                }
                case HookType.PRE_CREATE:
                    {
                        if (!ModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelElementHook.`);
                        }
                        this.preCreateHook(hook, parameters as CreateArgument, contextBundle.modelState);
                    }
                    break;
                case HookType.POST_CREATE:
                    {
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        if (!ModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelElementHook.`);
                        }
                        this.postCreateHook(hook, modelElement);
                    }
                    break;
                case HookType.CAN_DELETE:
                    {
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        if (!ModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelElementHook.`);
                        }
                        this.canDeleteHook(hook, modelElement);
                    }
                    break;
                case HookType.PRE_DELETE:
                    {
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        if (!ModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelElementHook.`);
                        }
                        this.preDeleteHook(hook, modelElement);
                    }
                    break;
                case HookType.POST_DELETE:
                    {
                        if (!ModelElementHook.is(hook) && !ModelFileHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelElementHook or ModelFileHook.`);
                        }
                        if (ModelElementHook.is(hook)) {
                            const modelElement = (parameters as DeleteArgument).deleted;
                            if (!modelElement && ModelElement.is(modelElement)) {
                                throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                            }
                            this.postDeleteHook(hook, modelElement);
                        } else if (ModelFileHook.is(hook)) {
                            const modelElement = (parameters as DeleteArgument).deleted;
                            this.postDeleteHook(hook, modelElement);
                        }
                    }
                    break;
                case HookType.CAN_SAVE: {
                    if (!GraphModelElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelElementHook.`);
                    }
                    const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                    if (!GraphModel.is(modelElement)) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is not a GraphModel.`);
                    }
                    return this.canSaveHook(hook, parameters as SaveModelFileArgument, modelElement);
                }
                case HookType.POST_SAVE:
                    {
                        if (!GraphModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!GraphModel.is(modelElement)) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is not a GraphModel.`);
                        }
                        this.postSaveHook(hook, parameters as SaveModelFileArgument, modelElement);
                    }
                    break;
                case HookType.ON_OPEN:
                    {
                        if (!GraphModelElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!GraphModel.is(modelElement)) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is not a GraphModel.`);
                        }
                        this.onOpenHook(hook, modelElement);
                    }
                    break;
                case HookType.POST_CONTENT_CHANGE:
                    {
                        if (!ModelFileHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelFileHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postContentChange(hook, modelElement);
                    }
                    break;
                case HookType.POST_PATH_CHANGE:
                    {
                        if (!ModelFileHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a ModelFileHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postPathChange(hook, modelElement);
                    }
                    break;
                case HookType.CAN_MOVE: {
                    if (!NodeElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not a NodeElementHook.`);
                    }
                    const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canMoveHook(hook, parameters as MoveArgument, modelElement);
                }
                case HookType.PRE_MOVE:
                    {
                        if (!NodeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a NodeElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.preMoveHook(hook, parameters as MoveArgument, modelElement);
                    }
                    break;
                case HookType.POST_MOVE:
                    {
                        if (!NodeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a NodeElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postMoveHook(hook, parameters as MoveArgument, modelElement);
                    }
                    break;
                case HookType.CAN_RESIZE: {
                    if (!NodeElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not a NodeElementHook.`);
                    }
                    const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canResizeHook(hook, parameters as ResizeArgument, modelElement);
                }
                case HookType.PRE_RESIZE:
                    {
                        if (!NodeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a NodeElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.preResizeHook(hook, parameters as ResizeArgument, modelElement);
                    }
                    break;
                case HookType.POST_RESIZE:
                    {
                        if (!NodeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a NodeElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postResizeHook(hook, parameters as ResizeArgument, modelElement);
                    }
                    break;
                case HookType.CAN_RECONNECT: {
                    if (!EdgeElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not an EdgeElementHook.`);
                    }
                    const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canReconnectHook(hook, parameters as ReconnectArgument, modelElement);
                }
                case HookType.PRE_RECONNECT:
                    {
                        if (!EdgeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an EdgeElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.preReconnectHook(hook, parameters as ReconnectArgument, modelElement);
                    }
                    break;
                case HookType.POST_RECONNECT:
                    {
                        if (!EdgeElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not an EdgeElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postReconnectHook(hook, parameters as ReconnectArgument, modelElement);
                    }
                    break;
                case HookType.CAN_SELECT: {
                    if (!GraphicalElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not a GraphicalElementHook.`);
                    }
                    const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canSelectHook(hook, parameters as SelectArgument, modelElement);
                }
                case HookType.POST_SELECT:
                    {
                        if (!GraphicalElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a GraphicalElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postSelectHook(hook, parameters as SelectArgument, modelElement);
                    }
                    break;
                case HookType.CAN_DOUBLE_CLICK: {
                    if (!GraphicalElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not a GraphicalElementHook.`);
                    }
                    const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canDoubleClickHook(hook, parameters as DoubleClickArgument, modelElement);
                }
                case HookType.POST_DOUBLE_CLICK:
                    {
                        if (!GraphicalElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a GraphicalElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postDoubleClickHook(hook, parameters as DoubleClickArgument, modelElement);
                    }
                    break;
                case HookType.CAN_LAYOUT: {
                    if (!GraphicalElementHook.is(hook)) {
                        throw new Error(`Hook of type ${type} could not be executed. hook is not a GraphicalElementHook.`);
                    }
                    const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                    if (!modelElement) {
                        throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                    }
                    return this.canLayoutHook(hook, parameters as LayoutArgument, modelElement);
                }
                case HookType.PRE_LAYOUT:
                    {
                        if (!GraphicalElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a GraphicalElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.preLayoutHook(hook, parameters as LayoutArgument, modelElement);
                    }
                    break;
                case HookType.POST_LAYOUT:
                    {
                        if (!GraphicalElementHook.is(hook)) {
                            throw new Error(`Hook of type ${type} could not be executed. hook is not a GraphicalElementHook.`);
                        }
                        const modelElement = contextBundle.modelState.index.findModelElement(parameters.modelElementId);
                        if (!modelElement) {
                            throw new Error(`Hook of type ${type} could not be executed. Modelelement is undefined.`);
                        }
                        this.postLayoutHook(hook, parameters as LayoutArgument, modelElement);
                    }
                    break;
            }
        } catch (e: any) {
            contextBundle.logger.error(e);
            this.notify(contextBundle.actionDispatcher, e.message, 'ERROR');
            return false;
        }
        return true;
    }

    /**
     * Attributes
     */

    private static canAttributeChangeHook(
        hook: AttributeHook<ModelElement>,
        parameter: AttributeChangeArgument,
        modelElement: ModelElement
    ): boolean {
        return !hook.canAttributeChange || hook.canAttributeChange(modelElement, parameter.operation);
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

    private static canCreateHook(hook: ModelElementHook<ModelElement>, parameters: CreateArgument, modelState: GraphModelState): boolean {
        if (!hook.canCreate) {
            return true;
        }
        switch (parameters.elementKind) {
            case 'Node': {
                const container = modelState.index.findModelElement(parameters.containerElementId);
                if (NodeElementHook.is(hook) && ModelElementContainer.is(container)) {
                    return hook.canCreate(parameters.elementTypeId, container, parameters.position);
                }
                return false;
            }
            case 'Edge': {
                const source = modelState.index.findNode(parameters.sourceElementId);
                const target = modelState.index.findNode(parameters.targetElementId);
                if (EdgeElementHook.is(hook) && source && target) {
                    return hook.canCreate(parameters.elementTypeId, source, target);
                }
                return false;
            }
            case 'UserDefinedType': {
                const host = modelState.index.findModelElement(parameters.elementTypeId);
                if (ModelElementHook.is(host)) {
                    return hook.canCreate(host);
                }
                return false;
            }
            case 'GraphModel': {
                if (GraphModelElementHook.is(hook)) {
                    return hook.canCreate(parameters.elementTypeId, parameters.path);
                }
                return false;
            }
        }
        return false;
    }

    private static preCreateHook(hook: ModelElementHook<any>, parameters: CreateArgument, modelState: GraphModelState): void {
        if (!hook.preCreate) {
            return;
        }
        switch (parameters.elementKind) {
            case 'Node':
                {
                    const container = modelState.index.findModelElement(parameters.containerElementId);
                    if (NodeElementHook.is(hook) && ModelElementContainer.is(container)) {
                        hook.preCreate(parameters.elementTypeId, container, parameters.position);
                    } else {
                        throw Error('Can not PreCreate node.');
                    }
                }
                break;
            case 'Edge':
                {
                    const source = modelState.index.findNode(parameters.sourceElementId);
                    const target = modelState.index.findNode(parameters.targetElementId);
                    if (EdgeElementHook.is(hook) && source && target) {
                        hook.preCreate(parameters.elementTypeId, source, target);
                    }
                }
                break;
            case 'UserDefinedType':
                {
                    const host = modelState.index.findModelElement(parameters.elementTypeId);
                    if (host && ModelElementHook.is(hook)) {
                        hook.preCreate(host);
                    }
                }
                break;
            case 'GraphModel':
                {
                    if (GraphModelElementHook.is(hook)) {
                        hook.preCreate(parameters.elementTypeId, parameters.path);
                    }
                }
                break;
        }
    }

    private static postCreateHook(hook: ModelElementHook<ModelElement>, modelElement: ModelElement): void {
        if (hook.postCreate) {
            hook.postCreate(modelElement);
        }
    }

    /**
     * Delete
     */

    private static canDeleteHook(hook: ModelElementHook<ModelElement>, modelElement: ModelElement): boolean {
        return !hook.canDelete || hook.canDelete(modelElement);
    }

    private static preDeleteHook(hook: ModelElementHook<ModelElement>, modelElement: ModelElement): void {
        if (hook.preDelete) {
            hook.preDelete(modelElement);
        }
    }

    private static postDeleteHook(hook: ModelElementHook<ModelElement> | ModelFileHook<ModelElement>, element: any): void {
        if (ModelElementHook.is(hook)) {
            hook.postDelete(element);
        } else if (ModelFileHook.is(hook)) {
            hook.postDelete(element);
        }
    }

    /**
     * Create
     */

    private static canSaveHook(
        hook: GraphModelElementHook<GraphModel>,
        parameters: SaveModelFileArgument,
        graphModel: GraphModel
    ): boolean {
        if (!hook.canSave) {
            return true;
        }
        return hook.canSave(graphModel, parameters.path);
    }

    private static postSaveHook(hook: GraphModelElementHook<GraphModel>, parameters: SaveModelFileArgument, graphModel: GraphModel): void {
        if (!hook.postSave) {
            return;
        }
        hook.postSave(graphModel, parameters.path);
    }

    private static onOpenHook(hook: GraphModelElementHook<GraphModel>, graphModel: GraphModel): void {
        if (!hook.onOpen) {
            return;
        }
        hook.onOpen(graphModel);
    }

    /**
     * Change
     */

    private static postPathChange(hook: ModelFileHook<ModelElement>, modelElement: ModelElement): void {
        if (hook.postPathChange) {
            hook.postPathChange(modelElement);
        }
    }

    private static postContentChange(hook: ModelFileHook<ModelElement>, modelElement: ModelElement): void {
        if (hook.postContentChange) {
            hook.postContentChange(modelElement);
        }
    }

    /**
     * Reconnect
     */

    private static canReconnectHook(hook: AbstractEdgeHook, parameters: ReconnectArgument, modelElement: ModelElement): boolean {
        const newSource: Cell<Node> = mapCell(parameters.sourceId, sourceID => {
            const source = modelElement.index.findNode(sourceID);
            if (source !== undefined) {
                return source;
            } else {
                throw new Error(`Undefined source ID in canReconnectHook parameters: ${sourceID}`);
            }
        });
        const newTarget: Cell<Node> = mapCell(parameters.targetId, targetID => {
            const target = modelElement.index.findNode(targetID);
            if (target !== undefined) {
                return target;
            } else {
                throw new Error(`Undefined target ID in canReconnectHook parameters: ${targetID}`);
            }
        });
        if (Edge.is(modelElement)) {
            return !hook.canAttributeChange || hook.canReconnect(modelElement, newSource, newTarget);
        } else {
            throw new Error('Could not execute canReconnectHook.');
        }
    }

    private static preReconnectHook(hook: AbstractEdgeHook, parameters: ReconnectArgument, modelElement: ModelElement): void {
        const newSource: Cell<Node> = mapCell(parameters.sourceId, sourceID => {
            const source = modelElement.index.findNode(sourceID);
            if (source !== undefined) {
                return source;
            } else {
                throw new Error(`Undefined source ID in preReconnectHook parameters: ${sourceID}`);
            }
        });
        const newTarget: Cell<Node> = mapCell(parameters.targetId, targetID => {
            const target = modelElement.index.findNode(targetID);
            if (target !== undefined) {
                return target;
            } else {
                throw new Error(`Undefined target ID in preReconnectHook parameters: ${targetID}`);
            }
        });
        if (hook.preReconnect && Edge.is(modelElement)) {
            hook.preReconnect(modelElement, newSource, newTarget);
        } else {
            throw new Error('Could not execute preReconnectHook.');
        }
    }

    private static postReconnectHook(hook: AbstractEdgeHook, parameters: ReconnectArgument, modelElement: ModelElement): void {
        const oldSource: Cell<Node> = mapCell(parameters.sourceId, sourceID => {
            const source = modelElement.index.findNode(sourceID);
            if (source !== undefined) {
                return source;
            } else {
                throw new Error(`Undefined source ID in postReconnectHook parameters: ${sourceID}`);
            }
        });
        const oldTarget: Cell<Node> = mapCell(parameters.targetId, targetID => {
            const target = modelElement.index.findNode(targetID);
            if (target !== undefined) {
                return target;
            } else {
                throw new Error(`Undefined target ID in canReconnectHook parameters: ${targetID}`);
            }
        });
        if (hook.postReconnect && Edge.is(modelElement)) {
            hook.postReconnect(modelElement, oldSource, oldTarget);
        } else {
            throw new Error('Could not execute postReconnectHook.');
        }
    }

    /**
     * Move
     */

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

    /**
     * Resize
     */

    private static canResizeHook(hook: AbstractNodeHook, parameters: ResizeArgument, modelElement: ModelElement | undefined): boolean {
        return (
            !hook.canAttributeChange || (Node.is(modelElement) && hook.canResize(modelElement, ResizeBounds.fromResizeArgument(parameters)))
        );
    }

    private static preResizeHook(hook: AbstractNodeHook, parameters: ResizeArgument, modelElement: ModelElement | undefined): void {
        if (hook.preResize && Node.is(modelElement)) {
            hook.preResize(modelElement, ResizeBounds.fromResizeArgument(parameters));
        }
    }

    private static postResizeHook(hook: AbstractNodeHook, parameters: ResizeArgument, modelElement: ModelElement | undefined): void {
        if (hook.postResize && Node.is(modelElement)) {
            hook.postResize(modelElement, ResizeBounds.fromResizeArgument(parameters));
        }
    }

    /**
     * Select
     */

    private static canSelectHook(
        hook: GraphicalElementHook<any>,
        parameters: SelectArgument,
        modelElement: ModelElement | undefined
    ): boolean {
        return !hook.canSelect || hook.canSelect(modelElement, parameters.isSelected);
    }

    private static postSelectHook(
        hook: GraphicalElementHook<any>,
        parameters: SelectArgument,
        modelElement: ModelElement | undefined
    ): void {
        if (hook.postSelect) {
            hook.postSelect(modelElement, parameters.isSelected);
        }
    }

    /**
     * DoubleClick
     */

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
     * DoubleClick
     */

    private static canLayoutHook(
        hook: GraphicalElementHook<any>,
        parameters: LayoutArgument,
        modelElement: ModelElement | undefined
    ): boolean {
        return !hook.canLayout || hook.canLayout(modelElement, parameters);
    }

    private static preLayoutHook(
        hook: GraphicalElementHook<any>,
        parameters: LayoutArgument,
        modelElement: ModelElement | undefined
    ): void {
        if (hook.preLayout) {
            hook.preLayout(modelElement, parameters);
        }
    }

    private static postLayoutHook(
        hook: GraphicalElementHook<any>,
        parameters: LayoutArgument,
        modelElement: ModelElement | undefined
    ): void {
        if (hook.postLayout) {
            hook.postLayout(modelElement, parameters);
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
