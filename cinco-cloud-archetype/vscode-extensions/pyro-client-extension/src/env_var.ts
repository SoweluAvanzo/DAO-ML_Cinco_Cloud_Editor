
// Allow untrusted ssl certificates in local environments. Otherwise node will
// throw an error "Error: unable to verify the first certificate" when it
// performs https requests to 'https://cinco-cloud'.
if (process.env.ENVIRONMENT === 'local') {
  process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = '0';
}

export const INTERNAL_USE_SSL = process.env.INTERNAL_USE_SSL === 'true';
export const INTERNAL_PYRO_HOST = process.env.INTERNAL_PYRO_HOST ?? 'localhost';
export const INTERNAL_PYRO_PORT = process.env.INTERNAL_PYRO_PORT ?? "8000";
export const INTERNAL_PYRO_SUBPATH = process.env.INTERNAL_PYRO_SUBPATH ?? '';
export const EXTERNAL_USE_SSL = process.env.EXTERNAL_USE_SSL === 'true';
export const EXTERNAL_PYRO_HOST = process.env.EXTERNAL_PYRO_HOST ?? 'localhost';
export const EXTERNAL_PYRO_PORT = process.env.EXTERNAL_PYRO_PORT ?? "8000";
export const EXTERNAL_PYRO_SUBPATH = process.env.EXTERNAL_PYRO_SUBPATH ?? '';
