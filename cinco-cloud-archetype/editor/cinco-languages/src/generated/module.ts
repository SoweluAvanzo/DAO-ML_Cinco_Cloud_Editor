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

import type {
    LangiumGeneratedServices,
    LangiumGeneratedSharedServices,
    LangiumSharedServices,
    LangiumServices,
    LanguageMetaData,
    Module
} from 'langium';
import { CincoAstReflection } from './ast.js';
import { MglGrammar, MslGrammar } from './grammar.js';

export const MglLanguageMetaData = {
    languageId: 'mgl',
    fileExtensions: ['.mgl'] as string[],
    caseInsensitive: false
} as const satisfies LanguageMetaData;

export const MslLanguageMetaData = {
    languageId: 'msl',
    fileExtensions: ['.style', '.msl'] as string[],
    caseInsensitive: false
} as const satisfies LanguageMetaData;

export const CincoGeneratedSharedModule: Module<LangiumSharedServices, LangiumGeneratedSharedServices> = {
    AstReflection: () => new CincoAstReflection()
};

export const MglGeneratedModule: Module<LangiumServices, LangiumGeneratedServices> = {
    Grammar: () => MglGrammar(),
    LanguageMetaData: () => MglLanguageMetaData,
    parser: {}
};

export const MslGeneratedModule: Module<LangiumServices, LangiumGeneratedServices> = {
    Grammar: () => MslGrammar(),
    LanguageMetaData: () => MslLanguageMetaData,
    parser: {}
};
