/* eslint-disable header/header */
import * as path from 'path';

import { DATABASE_PASSWORD, DATABASE_URL, DATABASE_USER, PYRO_PORT } from './environmentVars';

export const serverPath = path.resolve(__dirname, '..', '..', 'pyro-server');
export const serverFile = 'app.jar';
export const serverName = 'pyro-model-server';
export const cmdExec = 'java';
export const cmdArgs = [
    '-Dquarkus.datasource.jdbc.url="jdbc:postgresql://' + DATABASE_URL + '"',
    '-Dquarkus.datasource.username="' + DATABASE_USER + '"',
    '-Dquarkus.datasource.password="' + DATABASE_PASSWORD + '"',
    `-Dinfo.scce.pyro.auth.MainAppAuthClient/mp-rest/url=http${process.env.USE_SSL ? 's' : ''}://192.168.65.2:3000`,
    `-Dinfo.scce.pyro.style.MainAppStyleClient/mp-rest/url=http${process.env.USE_SSL ? 's' : ''}://192.168.65.2:3000`,
    '-Dquarkus.http.port=' + PYRO_PORT,
    '-jar'
];
export const cmdDebugArgs = [
    '-Ddebug'
].concat(cmdArgs);
