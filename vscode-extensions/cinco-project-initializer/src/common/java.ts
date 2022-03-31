// Java identifiers specification:
// https://docs.oracle.com/javase/specs/jls/se18/html/jls-3.html#jls-3.8

const keywords = [
    '_', 'abstract', 'assert', 'boolean', 'break', 'byte', 'case', 'catch',
    'char', 'class', 'const', 'continue', 'default', 'do', 'double', 'else',
    'enum', 'exports', 'extends', 'final', 'finally', 'float', 'for', 'goto',
    'if', 'implements', 'import', 'instanceof', 'int', 'interface', 'long',
    'module', 'native', 'new', 'non-sealed', 'open', 'opens', 'package',
    'permits', 'private', 'protected', 'provides', 'public', 'record',
    'requires', 'return', 'sealed', 'short', 'static', 'strictfp', 'super',
    'switch', 'synchronized', 'this', 'throw', 'throws', 'to', 'transient',
    'transitive', 'try', 'uses', 'var', 'void', 'volatile', 'while', 'with',
    'yield',
];

const literals = ['true', 'false', 'null'];

export function isValidIdentifier(input: string): boolean {
    return /^[a-zA-Z_$][a-zA-Z_$0-9]*$/.test(input)
        && !keywords.includes(input)
        && !literals.includes(input);
}

// Java package identifier specification:
// https://docs.oracle.com/javase/specs/jls/se18/html/jls-7.html#jls-7.4.1

export function isValidPackageIdentifier(input: string): boolean {
    return input.split('.').every(isValidIdentifier);
}
