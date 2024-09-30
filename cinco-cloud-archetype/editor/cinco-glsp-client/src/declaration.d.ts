// declaration.d.ts
declare module '*.css' {
    const content: Record<string, string>;
    export default content;
}

declare module '*.svg' {
    export default any;
}
