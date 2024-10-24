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
    CommandExecutionContext,
    CommandReturn,
    Dimension,
    FeedbackAwareUpdateModelCommand,
    GModelRoot,
    isViewport,
    MatchResult,
    ModelMatcher,
    SetModelCommand
} from '@eclipse-glsp/client';
import { injectable } from 'inversify';

@injectable()
export class CincoUpdateModelCommand extends FeedbackAwareUpdateModelCommand {
    protected override performUpdate(oldRoot: GModelRoot, newRoot: GModelRoot, context: CommandExecutionContext): CommandReturn {
        if ((this.action.animate === undefined || this.action.animate) && oldRoot.id === newRoot.id) {
            let matchResult: MatchResult;
            if (this.action.matches === undefined) {
                const matcher = new ModelMatcher();
                matchResult = matcher.match(oldRoot, newRoot);
            } else {
                matchResult = this.convertToMatchResult(this.action.matches, oldRoot, newRoot);
            }
            const animationOrRoot = this.computeAnimation(newRoot, matchResult, context);
            if (animationOrRoot instanceof Animation) {
                return (animationOrRoot as any).start();
            } else {
                return animationOrRoot as CommandReturn;
            }
        } else {
            if (oldRoot.type === newRoot.type && Dimension.isValid(oldRoot.canvasBounds)) {
                newRoot.canvasBounds = oldRoot.canvasBounds;
            }
            if (isViewport(oldRoot) && isViewport(newRoot)) {
                newRoot.zoom = oldRoot.zoom;
                newRoot.scroll = oldRoot.scroll;
            }
            return newRoot;
        }
    }
}

@injectable()
export class CincoSetModelCommand extends SetModelCommand {
    override execute(context: CommandExecutionContext): GModelRoot {
        this.oldRoot = context.modelFactory.createRoot(context.root);
        this.newRoot = context.modelFactory.createRoot(this.action.newRoot);
        if (this.oldRoot && (this.oldRoot as any).zoom && (this.oldRoot as any).scroll) {
            // cinco-glsp-fix: preserve zoom scroll
            (this.newRoot as any).zoom = (this.oldRoot as any).zoom;
            (this.newRoot as any).scroll = (this.oldRoot as any).scroll;
        }
        return this.newRoot;
    }
}
