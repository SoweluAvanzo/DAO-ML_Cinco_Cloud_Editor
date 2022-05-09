
// Allow untrusted ssl certificates in local environments. Otherwise node will
// throw an error "Error: unable to verify the first certificate" when it
// performs https requests to 'https://cinco-cloud'.
if (process.env.ENVIRONMENT === 'local') {
  process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = '0';
}

export const PYRO_PORT = process.env.PYRO_PORT? process.env.PYRO_PORT: "8000";
export const PYRO_HOST = process.env.PYRO_HOST? process.env.PYRO_HOST: 'localhost';
export const PYRO_SUBPATH = process.env.PYRO_SUBPATH? process.env.PYRO_SUBPATH: '';
export const USE_SSL = process.env.USE_SSL? process.env.USE_SSL: false;
