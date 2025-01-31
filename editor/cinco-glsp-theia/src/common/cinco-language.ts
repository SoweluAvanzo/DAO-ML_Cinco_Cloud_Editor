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
import { DIAGRAM_TYPE, getGraphTypes } from '@cinco-glsp/cinco-glsp-common';
import { GLSPDiagramLanguage } from '@eclipse-glsp/theia-integration/lib/common';

export function getDiagramConfiguration(): GLSPDiagramLanguage {
    const fileExtensions = getGraphTypes()?.map(g => g.diagramExtension) ?? [];
    return {
        contributionId: 'cinco',
        diagramType: DIAGRAM_TYPE,
        fileExtensions: fileExtensions,
        label: 'Cinco Diagram',
        iconClass: 'codicon codicon-type-hierarchy-sub'
    };
}
