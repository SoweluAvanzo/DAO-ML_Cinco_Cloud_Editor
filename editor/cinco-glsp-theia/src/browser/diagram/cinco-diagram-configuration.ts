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

import { GLSPDiagramConfiguration } from '@eclipse-glsp/theia-integration/lib/browser';
import { Container as ContainerInversifyTheia, injectable } from '@theia/core/shared/inversify';
import { Container } from 'inversify';

import { getDiagramConfiguration } from '../../common/cinco-language';
import { ContainerConfiguration, FeatureModule } from '@eclipse-glsp/client';
import { initializeCincoDiagramContainer, EnvironmentProvider } from '@cinco-glsp/cinco-glsp-client';
import { TheiaEnvironmentProvider } from '../cinco-glsp/theia-environment-provider';

@injectable()
export class CincoDiagramConfiguration extends GLSPDiagramConfiguration {
    diagramType: string = getDiagramConfiguration().diagramType;

    override configureContainer(
        container: ContainerInversifyTheia,
        ...containerConfiguration: ContainerConfiguration
    ): ContainerInversifyTheia {
        const cinco_bindings = new FeatureModule((bind, unbind, isBound, rebind) => {
            const context = { bind, unbind, isBound, rebind };
            context.bind(EnvironmentProvider).to(TheiaEnvironmentProvider).inSingletonScope();
        });
        initializeCincoDiagramContainer(
            container as unknown as Container,
            {
                add: [cinco_bindings],
                remove: []
            },
            ...containerConfiguration
        );
        return container;
    }
}
