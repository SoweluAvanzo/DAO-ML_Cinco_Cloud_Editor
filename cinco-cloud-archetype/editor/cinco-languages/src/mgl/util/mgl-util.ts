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
import { Edge, EdgeElementConnection } from '../../generated/ast.js';
import { Reference } from 'langium';

export function getConnectingEdges(edgeElementConnection: EdgeElementConnection): Edge[] {
    const localConnections = edgeElementConnection.localConnection;
    const result = localConnections
        .flatMap((localConnection: Reference<Edge>) => localConnection.$refNode?.element as Edge)
        .filter((entry: Edge) => entry !== undefined);
    if (result.length > 1) {
        for (const entry of result) {
            console.log(entry.name);
            console.log(entry);
        }
    }
    return result;
}
