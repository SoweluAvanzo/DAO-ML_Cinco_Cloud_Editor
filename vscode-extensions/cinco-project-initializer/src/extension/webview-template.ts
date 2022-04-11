import * as vscode from 'vscode'

export function getWebviewContent(
    webview: vscode.Webview,
    styleUrl: vscode.Uri,
    scriptUrl: vscode.Uri
) {
    return `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta
        http-equiv="Content-Security-Policy"
        content="default-src 'none'; img-src ${webview.cspSource}; script-src ${webview.cspSource}; style-src ${webview.cspSource};"
    />
    <title>Project Initialization</title>
    <link rel="stylesheet" type="text/css" href="${styleUrl}" media="screen" />
</head>
<body>
    <script type="module" src="${scriptUrl}"></script>
</body>
</html>`;
}
