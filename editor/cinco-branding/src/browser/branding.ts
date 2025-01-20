/*!
 * Copyright (c) 2019-2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */

import { FrontendApplicationContribution } from '@theia/core/lib/browser';
import { ThemeService } from '@theia/core/lib/browser/theming';
import { inject, injectable } from 'inversify';

import dark = require('../../style/variables-dark.useable.css');
import light = require('../../style/variables-light.useable.css');

@injectable()
export class ThemeHandler implements FrontendApplicationContribution {
    @inject(ThemeService)
    protected readonly themeService: ThemeService | undefined;

    onStart(): void {
        if (!this.themeService) {
            throw new Error('ThemeService could not be injected!');
        }
        this.updateTheme();
        this.themeService.onDidColorThemeChange(() => this.updateTheme());
    }

    updateTheme(): void {
        if (!this.themeService) {
            throw new Error('ThemeService could not be injected!');
        }
        const theme = this.themeService.getCurrentTheme().id;
        if (theme === 'dark') {
            light.unuse();
            dark.use();
        } else if (theme === 'light') {
            dark.unuse();
            light.use();
        }
    }
}
