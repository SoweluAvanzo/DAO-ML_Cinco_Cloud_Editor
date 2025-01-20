/********************************************************************************
 * Copyright (c) 2020-2022 Cinco Cloud and others.
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
    PropertyEditAction,
    PropertyViewRequestAction,
    PropertyViewMessage,
    PropertyViewResponseAction
} from '@cinco-glsp/cinco-glsp-common';
import {
    Action,
    Tool,
    IActionHandler,
    ICommand,
    MouseListener,
    GChildElement,
    GModelElement,
    GLSPActionDispatcher,
    TYPES,
    MouseTool,
    IActionDispatcher
} from '@eclipse-glsp/client';
import { inject, injectable, postConstruct } from 'inversify';
import { EnvironmentProvider, IEnvironmentProvider } from '../../api/environment-provider';

@injectable()
export class PropertyViewResponseActionHandler implements IActionHandler {
    @inject(EnvironmentProvider) environmentProvider: IEnvironmentProvider;

    handle(action: PropertyViewResponseAction): void | Action | ICommand {
        this.environmentProvider.provideProperties(action);
    }
}

@injectable()
export class PropertyViewTool implements Tool {
    @inject(TYPES.IActionDispatcher) protected actionDispatcher: GLSPActionDispatcher;
    @inject(MouseTool) protected mouseTool: MouseTool;
    static readonly ID = 'property-view-tool';
    protected mouseListenenr: MouseListener;

    @postConstruct()
    initEditAction(): void {
        window.addEventListener('message', ({ data: message }: { data: PropertyViewMessage }) => {
            if (message.kind === 'editProperty') {
                const { modelElementId, pointer, name, change } = message;
                const action = PropertyEditAction.create(modelElementId, pointer, name, change);
                this.actionDispatcher.dispatch(action);
            }
        });
    }

    get id(): string {
        return PropertyViewTool.ID;
    }

    protected createPropertyViewMouseListener(): MouseListener {
        return new PropertyViewMouseListener(this.actionDispatcher);
    }

    enable(): void {
        this.mouseListenenr = this.createPropertyViewMouseListener();
        this.mouseTool.register(this.mouseListenenr);
    }

    disable(): void {
        this.mouseTool.deregister(this.mouseListenenr);
    }
}

/**
 * Listener
 *
 * the listeners will heandle the creation of the action.
 * Theses actions will be handled to other tools (e.g. mouseTool and keyTool),
 * who will dispatch the action further to the backend.
 */

@injectable()
export class PropertyViewMouseListener extends MouseListener {
    protected readonly actionDispatcher: IActionDispatcher;
    lastTarget: string;

    constructor(actionDispatcher: IActionDispatcher) {
        super();
        this.actionDispatcher = actionDispatcher;
    }

    override mouseUp(target: GModelElement, event: MouseEvent): (Action | Promise<Action>)[] {
        if (target.type === 'label' && target instanceof GChildElement) {
            target = target.parent;
        }
        if (target.id !== this.lastTarget) {
            this.lastTarget = target.id;
            this.requestProperties(this.lastTarget);
            return [];
        }
        return [];
    }

    async requestProperties(selectedElementId: string): Promise<void> {
        const propertyViewAction = PropertyViewRequestAction.create(selectedElementId);
        this.actionDispatcher.dispatch(propertyViewAction);
    }
}
