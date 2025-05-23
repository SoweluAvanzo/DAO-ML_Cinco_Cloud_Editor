/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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

import { TranspilationMode } from './protocol/args-protocol';

export const META_LANGUAGES_FRONTEND_FOLDER = 'languages';
export const META_LANGUAGES_FOLDER = 'cinco-glsp-server/languages';
export const WORKSPACE_FOLDER = 'workspace';
export const SERVER_LANGUAGES_FOLDER = 'cinco-glsp-server/lib/languages'; // @deprecated: this should not be used anymore
export const META_FILE_TYPES = ['.json', '.mgl', '.msl', '.style'];
export const RESOURCE_TYPES = ['.css'];
export const SUPPORTED_DYNAMIC_FILE_TYPES = ['.js'];
export const DIAGRAM_TYPE = 'cinco-diagram';

// environment and args keys
export const META_DEV_MODE = 'META_DEV_MODE';
export const PORT_KEY = 'CINCO_GLSP';
export const WEBSOCKET_PATH_KEY = 'CINCO_GLSP_WS_PATH';
export const LANGUAGES_FOLDER_KEY = 'META_LANGUAGES_FOLDER';
export const WORKSPACE_FOLDER_KEY = 'WORKSPACE_FOLDER';
export const ROOT_FOLDER_KEY = 'ROOT_FOLDER';
export const WEBSOCKET_PORT_KEY = 'WEBSOCKET_PORT';
export const WEB_SERVER_PORT_KEY = 'WEB_SERVER_PORT';
export const WEBSOCKET_HOST_MAPPING = 'WEBSOCKET_HOST_MAPPING';
export const WEBSERVER_HOST_MAPPING = 'WEBSERVER_HOST_MAPPING';
export const TRANSPILATION_MODE_KEY = 'TRANSPILATION_MODE';
export const USE_SSL = 'USE_SSL';
export const DEFAULT_WEBSOCKET_PATH = DIAGRAM_TYPE;
export const DEFAULT_THEIA_PORT = 3000;
export const DEFAULT_WEB_SERVER_PORT = 3003;
export const DEFAULT_SERVER_PORT = 5007;
export const DEFAULT_TRANSPILATION_MODE = TranspilationMode.NONE;
