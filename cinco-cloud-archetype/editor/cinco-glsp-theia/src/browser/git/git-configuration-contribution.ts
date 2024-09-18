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
import { FrontendApplication, FrontendApplicationContribution, WidgetManager } from '@theia/core/lib/browser';
import { inject, injectable } from '@theia/core/shared/inversify';
import { ScmHistoryContribution } from '@theia/scm-extra/lib/browser/history/scm-history-contribution';
import { ScmContribution } from '@theia/scm/lib/browser/scm-contribution';
import { FileNavigatorContribution } from '@theia/navigator/lib/browser/navigator-contribution';
import { MaybePromise } from '@theia/core';

@injectable()
export class GitConfigurationContribution implements FrontendApplicationContribution {
    @inject(WidgetManager) protected readonly widgetManager: WidgetManager;
    @inject(ScmHistoryContribution)
    protected scmHistory: ScmHistoryContribution;
    @inject(ScmContribution)
    protected scmContribution: ScmContribution;
    @inject(FileNavigatorContribution)
    protected fileNavigatorContribution: FileNavigatorContribution;

    onStart?(app: FrontendApplication): MaybePromise<void> {
        this.scmContribution.openView({ toggle: false, activate: false }).then(_ => {
            this.scmHistory.openView({ toggle: false, activate: false }).then(_ => {
                this.fileNavigatorContribution.openView({ toggle: false, activate: false });
            });
        });
    }
}
