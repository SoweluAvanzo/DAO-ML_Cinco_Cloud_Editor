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

import { jsonEqual, mapRecord } from './json-utilities';

type Conflict = Readonly<{
    tag: 'eager-merge-conflict';
    ancestor: any;
    versionA: any;
    versionB: any;
}>;

export type Merger = (ancestor: any, versionA: any, versionB: any) => any;

export function mergeRecord(mergers: Record<string, Merger>): Merger {
    return (ancestor, versionA, versionB) => {
        validateNoUnknownKeysForRecordMerger(mergers, ancestor);
        validateNoUnknownKeysForRecordMerger(mergers, versionA);
        validateNoUnknownKeysForRecordMerger(mergers, versionB);
        return mapRecord(mergers, (merger, key) => merger(ancestor[key], versionA[key], versionB[key]));
    };
}

function validateNoUnknownKeysForRecordMerger(mergers: Record<string, Merger>, record: Record<string, any>): void {
    for (const key of Object.keys(record)) {
        if (!(key in mergers)) {
            throw new Error(`Key ${key} has no merger defined.`);
        }
    }
}

export function mergeEager(ancestor: any, versionA: any, versionB: any): any {
    if (jsonEqual(versionA, versionB)) {
        return versionA;
    } else {
        const conflict: Conflict = {
            tag: 'eager-merge-conflict',
            ancestor,
            versionA,
            versionB
        };
        return conflict;
    }
}
