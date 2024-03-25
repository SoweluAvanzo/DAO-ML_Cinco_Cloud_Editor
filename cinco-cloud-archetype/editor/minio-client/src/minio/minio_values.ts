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

export const MINIO_HOST = process.env.MINIO_HOST ?? 'minio-service';
export const MINIO_PORT = process.env.MINIO_PORT ?? '9000';
export const MINIO_ACCESS_KEY = process.env.MINIO_ACCESS_KEY ?? '';
export const MINIO_SECRET_KEY = process.env.MINIO_SECRET_KEY ?? '';
export const MINIO_RESOURCE_ID = process.env.MINIO_RESOURCE_ID ?? '';
