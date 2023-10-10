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
import { META_FILE_TYPES, SUPPORTED_DYNAMIC_FILE_TYPES } from '@cinco-glsp/cinco-glsp-common';
import 'reflect-metadata';
import { launch } from './app';
import { MetaSpecificationLoader } from './meta/meta-specification-loader';

loadMetaFiles();
launch();

function loadMetaFiles(): void {
    /**
    * Load meta specification if available
    * Load all files from language-folder.
    * These files contain the language-designer defined hooks, actions, generators, etc.
    */
    MetaSpecificationLoader.load(META_FILE_TYPES);
    MetaSpecificationLoader.loadClassFiles(SUPPORTED_DYNAMIC_FILE_TYPES);
}
