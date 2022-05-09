// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { httpProtocol, wsProtocol, host } from './vars';

export const environment = {
  production: false,
  baseUrl: `${httpProtocol}//${host}`,
  webSocketUrl: `${wsProtocol}//${host}/api/ws`,
  apiUrl: `${httpProtocol}//${host}/api`
};
