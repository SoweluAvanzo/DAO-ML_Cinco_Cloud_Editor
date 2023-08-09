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
    CompositionSpecification,
    MetaSpecification,
    getGraphTypes
} from '@cinco-glsp/cinco-glsp-server/lib/src/shared/meta-specification';
import { GLSPDiagramLanguage } from '@eclipse-glsp/theia-integration';
import { CommandContribution, CommandRegistry } from '@theia/core';
import { injectable } from 'inversify';
import '../../css/cinco.css';

export function getDiagramConfiguration(): GLSPDiagramLanguage {
    const fileExtensions = getGraphTypes()?.map(g => g.diagramExtension) ?? [];
    return {
        contributionId: 'cinco',
        diagramType: 'cinco-diagram',
        fileExtensions: fileExtensions,
        label: 'Cinco Diagram',
        iconClass: 'codicon codicon-type-hierarchy-sub'
    };
}

export interface LanguageUpdateMessage {
    metaSpecification: CompositionSpecification;
}

@injectable()
export class LanguageUpdateCommand implements CommandContribution {
    LANGUAGE_UPDATE_COMMAND = { id: 'cinco.language_update' };

    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(this.LANGUAGE_UPDATE_COMMAND, {
            execute: (message: LanguageUpdateMessage) => this.updateLanguage(message)
        });
    }

    updateLanguage(message: LanguageUpdateMessage): void {
        // update MetaSpecification
        MetaSpecification.merge(message.metaSpecification);
    }
}
