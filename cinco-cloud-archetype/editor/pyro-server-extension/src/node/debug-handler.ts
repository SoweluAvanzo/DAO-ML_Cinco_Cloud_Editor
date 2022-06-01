/* eslint-disable header/header */
export function isDebugging(): boolean {
    const debugging = process.env.CINCO_CLOUD_DEBUG;
    return debugging === 'true';
}
