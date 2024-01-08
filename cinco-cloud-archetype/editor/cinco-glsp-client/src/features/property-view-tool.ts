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
    PropertyViewAction,
    PropertyViewMessage,
    PropertyViewResponseAction,
    PropertyViewUpdateCommand
} from '@cinco-glsp/cinco-glsp-common';
import {
    Action,
    BaseGLSPTool,
    IActionHandler,
    ICommand,
    MouseListener,
    RankingMouseTool,
    SChildElement,
    SModelElement
} from '@eclipse-glsp/client';
import { CommandService } from '@theia/core';
import { inject, injectable, optional, postConstruct } from 'inversify';

@injectable()
export class PropertyViewResponseActionHandler implements IActionHandler {
    @inject(CommandService) @optional() commandService: CommandService;

    handle(action: PropertyViewResponseAction): void | Action | ICommand {
        if (this.commandService)
            this.commandService.executeCommand(
                PropertyViewUpdateCommand.id,
                action.modelElementIndex,
                action.modelType,
                action.modelElementId,
                action.attributeDefinitions,
                action.values
            );
    }
}

@injectable()
export class PropertyViewTool extends BaseGLSPTool {
    static readonly ID = 'property-view-tool';

    @inject(RankingMouseTool)
    protected editLabelMouseListener: MouseListener;

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
        return new PropertyViewMouseListener();
    }

    enable(): void {
        this.editLabelMouseListener = this.createPropertyViewMouseListener();
        this.mouseTool.register(this.editLabelMouseListener);
    }

    disable(): void {
        this.mouseTool.deregister(this.editLabelMouseListener);
    }
}

/**
 * Listener
 *
 * the listeners will heandle the creation of the action.
 * Theses actions will be handled to other tools (e.g. mouseTool and keyTool),
 * who will dispatch the action further to the backend.
 */

export class PropertyViewMouseListener extends MouseListener {
    lastTarget: string;

    override mouseUp(target: SModelElement, event: MouseEvent): (Action | Promise<Action>)[] {
        if (target.type === 'label' && target instanceof SChildElement) {
            target = target.parent;
        }
        if (target.id !== this.lastTarget) {
            this.lastTarget = target.id;
            return [PropertyViewAction.create(target.id)];
        }
        return [];
    }
}
