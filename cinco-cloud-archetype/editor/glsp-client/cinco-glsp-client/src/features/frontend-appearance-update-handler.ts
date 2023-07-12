/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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
    ContainerShape,
    EdgeStyle,
    MultiText,
    NodeStyle,
    Polyline,
    Text
} from '@cinco-glsp/cinco-glsp-server/lib/src/shared/meta-specification';
import { ApplyAppearanceUpdateAction } from '@cinco-glsp/cinco-glsp-server/lib/src/shared/protocol/appearance-provider-protocol';
import { CommandExecutionContext, CommandReturn, ILogger, SModelElement, SModelRoot, SystemCommand, TYPES } from '@eclipse-glsp/client';
import { inject, injectable } from 'inversify';
import { CincoEdge, CincoNode } from '../model/model';

@injectable()
export class ApplyAppearanceUpdateCommand extends SystemCommand {
    static readonly KIND = ApplyAppearanceUpdateAction.KIND;
    @inject(TYPES.ILogger)
    protected logger: ILogger;
    @inject(TYPES.Action)
    protected readonly action: ApplyAppearanceUpdateAction;

    constructor() {
        super();
    }

    execute(context: CommandExecutionContext): CommandReturn {
        const model: SModelRoot = context.root;
        const modelElement: SModelElement | undefined = model.index.getById(this.action.modelElementId);

        if (modelElement && (modelElement instanceof CincoNode || modelElement instanceof CincoEdge)) {
            if (this.action.appearance) {
                const newAppearance = this.action.appearance;
                if (modelElement instanceof CincoNode) {
                    const style: NodeStyle = { ...modelElement.style } as NodeStyle;
                    const shape = style.shape;
                    if (shape && (ContainerShape.is(shape) || Text.is(shape) || MultiText.is(shape) || Polyline.is(shape))) {
                        (style.shape as ContainerShape | Text | MultiText | Polyline).appearance = newAppearance;
                        modelElement.style = style;
                    }
                } else if (modelElement instanceof CincoEdge) {
                    const style: EdgeStyle = { ...modelElement.style } as EdgeStyle;
                    style.appearance = newAppearance;
                    modelElement.style = style;
                } else {
                    throw new Error('Cannot apply AppearanceUpdate. ModelElement is neither CincoNode nor CincoEdge!');
                }
            }
            if (this.action.cssClasses) {
                const cssClasses = this.action.cssClasses;
                if (modelElement) {
                    this.logger.info(this, 'received: ' + cssClasses);
                    modelElement.cssClasses = cssClasses;
                }
            }
        }
        return this.redo(context);
    }

    undo(context: CommandExecutionContext): CommandReturn {
        return context.root;
    }

    redo(context: CommandExecutionContext): CommandReturn {
        return context.root;
    }
}
