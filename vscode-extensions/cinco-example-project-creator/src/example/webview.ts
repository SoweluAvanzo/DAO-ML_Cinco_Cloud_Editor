export function getWebviewContent() {
    return `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Project Initialization</title>
    <style>
        html, body {
            height: 100%;
            margin: 0;
            padding: 0;
        }
        body {
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .type-button {
            display: block;
            width: 400px;
            padding: 16px;
        }

        .or {
            text-align: center;
            margin: 12px 0;
        }
    </style>
</head>
<body>
    <form>
        <button class="type-button" id="create-new-cinco-language-button">
            Create New Cinco Language…
        </button>
        <p class="or">or</p>
        <button class="type-button" id="initialize-example-project-button">
            Initialize Example Project
        </button>
    </form>
    <script>
        (() => {
            const vscode = acquireVsCodeApi();

            document
                .getElementById('initialize-example-project-button')
                .addEventListener('click', () => {
                    vscode.postMessage(null);
                });
        })()
    </script>
</body>
</html>`;
}
