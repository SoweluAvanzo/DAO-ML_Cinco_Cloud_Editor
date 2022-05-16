/* eslint-disable header/header */
import * as path from 'path';

import { CINCO_CLOUD_HOST, CINCO_CLOUD_PORT, DATABASE_PASSWORD, DATABASE_URL, DATABASE_USER, PYRO_PORT } from './environmentVars';

export const serverPath = path.resolve(__dirname, '..', '..', 'pyro-server');
export const serverFile = 'app.jar';
export const cmdExec = 'java';
export const cmdArgs = [
    '-Dquarkus.datasource.jdbc.url="jdbc:postgresql://' + DATABASE_URL + '"',
    '-Dquarkus.datasource.username="' + DATABASE_USER + '"',
    '-Dquarkus.datasource.password="' + DATABASE_PASSWORD + '"',
    `-Dinfo.scce.pyro.auth.MainAppAuthClient/mp-rest/url=http${process.env.USE_SSL ? 's' : ''}://${CINCO_CLOUD_HOST}:${CINCO_CLOUD_PORT}`,
    `-Dinfo.scce.pyro.style.MainAppStyleClient/mp-rest/url=http${process.env.USE_SSL ? 's' : ''}://${CINCO_CLOUD_HOST}:${CINCO_CLOUD_PORT}`,
    '-Dquarkus.http.port=' + PYRO_PORT,
    '-jar'
];
export const cmdDebugArgs = [
    '-Ddebug'
].concat(cmdArgs);
