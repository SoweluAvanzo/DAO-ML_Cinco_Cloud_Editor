/********************************************************************************
 * Copyright (c) 2019-2023 EclipseSource and others.
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
import { initializeCincoDiagramContainer, DefaultEnvironmentProvider, EnvironmentProvider } from '@cinco-glsp/cinco-glsp-client';
import {
    bindOrRebind,
    ConsoleLogger,
    createDiagramOptionsModule,
    IDiagramOptions,
    LogLevel,
    STANDALONE_MODULE_CONFIG,
    TYPES,
    toolPaletteModule,
    FeatureModule,
    FocusTrackerTool,
    configureElementNavigationTool,
    configureFocusTrackerTool,
    configureMoveZoom,
    configureResizeTools,
    configureSearchPaletteModule,
    configureToastTool,
    configureViewKeyTools,
    contextMenuModule
} from '@eclipse-glsp/client';
import { configureShortcutHelpTool } from '@eclipse-glsp/client/lib/features/accessibility/key-shortcut/di.config';
import { configureKeyboardControlTools } from '@eclipse-glsp/client/lib/features/accessibility//keyboard-pointer/keyboard-pointer-module';
import { Container } from 'inversify';
import '../css/diagram.css';
import { CincoFocusTrackerTool } from './tools/cinco-focus-tracker-tool';
import { CincoContextMenu, CincoContextMenuService } from './context-menu/cinco-context-menu';
export default function createContainer(options: IDiagramOptions): Container {
    // Add features
    const cinco_bindings = new FeatureModule((bind, unbind, isBound, rebind) => {
        const context = { bind, unbind, isBound, rebind };
        context.unbind(FocusTrackerTool);
        context.bind(FocusTrackerTool).to(CincoFocusTrackerTool);
        context.bind(EnvironmentProvider).to(DefaultEnvironmentProvider).inSingletonScope();
        context.bind(TYPES.IContextMenuService).to(CincoContextMenuService);
        context.bind(CincoContextMenu).to(CincoContextMenu).inSingletonScope();
    });
    const accessibilityModule = new FeatureModule((bind, unbind, isBound, rebind) => {
        const context = { bind, unbind, isBound, rebind };
        configureResizeTools(context);
        configureViewKeyTools(context);
        configureMoveZoom(context);
        configureSearchPaletteModule(context);
        configureShortcutHelpTool(context);
        configureKeyboardControlTools(context);
        configureElementNavigationTool(context);
        configureFocusTrackerTool(context);
        configureToastTool(context);
    });
    // Build Container
    const container = initializeCincoDiagramContainer(
        new Container(),
        createDiagramOptionsModule(options),
        {
            add: [accessibilityModule, cinco_bindings, contextMenuModule],
            remove: [toolPaletteModule]
        },
        STANDALONE_MODULE_CONFIG
    );
    bindOrRebind(container, TYPES.ILogger).to(ConsoleLogger).inSingletonScope();
    bindOrRebind(container, TYPES.LogLevel).toConstantValue(LogLevel.info);
    container.bind(TYPES.IMarqueeBehavior).toConstantValue({ entireEdge: true, entireElement: true });
    return container;
}
