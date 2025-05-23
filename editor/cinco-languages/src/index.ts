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
import 'reflect-metadata';

export { MglAnnotations } from './mgl/language/mgl-annotations';
export * from './mgl/cli';
export * as MglModule from './mgl/language/mgl-module';
export * as MglValidator from './mgl/language/mgl-validator';
export * as MslModule from './msl/language/msl-module';
export * as MslValidator from './msl/language/msl-validator';
export * as mglLoader from './mgl/cli/index';
export * as LanguageMetaData from './generated/module';
