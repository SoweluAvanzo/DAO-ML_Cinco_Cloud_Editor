/* eslint-disable header/header */
// Environment vars
export const DATABASE_URL = process.env.DATABASE_URL ? process.env.DATABASE_URL : 'localhost:5432/quarkus_test';
export const DATABASE_USER = process.env.DATABASE_USER ? process.env.DATABASE_USER : 'quarkus_test';
export const DATABASE_PASSWORD = process.env.DATABASE_PASSWORD ? process.env.DATABASE_PASSWORD : 'quarkus_test';
export const PYRO_PORT = process.env.PYRO_PORT ? process.env.PYRO_PORT : '8000';
