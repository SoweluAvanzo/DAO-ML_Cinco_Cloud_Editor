/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud and others.
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
import { Attribute, CustomType } from '@cinco-glsp/cinco-glsp-common/lib/meta-specification';
import { ModelElementIndex } from '@cinco-glsp/cinco-glsp-common/lib/protocol/property-model';
import { PropertyViewUpdateCommand } from '@cinco-glsp/cinco-glsp-common/lib/protocol/property-protocol';
import { CommandContribution, CommandRegistry } from '@theia/core';
import { inject, injectable } from 'inversify';

import { PropertyDataHandler } from './property-data-handler';

@injectable()
export class PropertyUpdateCommandContribution implements CommandContribution {
    @inject(PropertyDataHandler) propertyDataHandler: PropertyDataHandler;

    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(PropertyViewUpdateCommand, {
            execute: (
                modelElementIndex: ModelElementIndex,
                modelType: string,
                modelElementId: string,
                attributeDefinitions: Attribute[],
                customTypeDefinitions: CustomType[],
                values: any
            ) => {
                this.propertyDataHandler.updatePropertySelection(
                    modelElementIndex,
                    modelType,
                    modelElementId,
                    attributeDefinitions,
                    customTypeDefinitions,
                    values
                );
            }
        });
    }
}
