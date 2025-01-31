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
    eagerMerger,
    cellMerger,
    entityListMerger,
    Merger,
    recordMerger,
    defaultMerger,
    recursiveMerger,
    arbitraryMerger
} from './combinators';

export function graphMerger(): Merger {
    return recordMerger({
        id: eagerMerger(),
        _containments: entityListMerger(nodeMerger()),
        _edges: entityListMerger(edgeMerger()),
        type: eagerMerger(),
        _attributes: eagerMerger(),
        _view: eagerMerger()
    });
}

export function nodeMerger(): Merger {
    return recordMerger({
        type: eagerMerger(),
        _position: arbitraryMerger(),
        _size: arbitraryMerger(),
        _attributes: eagerMerger(),
        _primeReference: eagerMerger(),
        _containments: entityListMerger(recursiveMerger(nodeMerger))
    });
}

export function edgeMerger(): Merger {
    return recordMerger({
        type: eagerMerger(),
        _attributes: eagerMerger(),
        sourceID: cellMerger(),
        targetID: cellMerger(),
        _routingPoints: defaultMerger([])
    });
}
