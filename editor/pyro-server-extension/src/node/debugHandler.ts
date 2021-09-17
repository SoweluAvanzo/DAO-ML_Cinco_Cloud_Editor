/* eslint-disable header/header */
export function isDebugging(): boolean {
    const debugging = process.env.CINCO_CLOUD_DEBUG;
    if (debugging === 'true') {
        return true;
    }
    return false;
}
