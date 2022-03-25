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

        .action-button {
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
    <script>
        (() => {
            const vscode = acquireVsCodeApi();

            const previousState = vscode.getState();
            let state = previousState ? previousState : {tag:'Initial'}

            renderPage();

            function renderPage() {
                document.body.innerHTML = '';

                switch (state.tag) {
                    case 'Initial':
                        const form = document.createElement('form');
                        document.body.appendChild(form);

                        const createScaffoldButton =
                            document.createElement('button');
                        createScaffoldButton.type = 'button';
                        createScaffoldButton.className = 'action-button';
                        createScaffoldButton.innerText =
                            "Create New Cinco Language…";
                        form.appendChild(createScaffoldButton);

                        const orLabel = document.createElement('p');
                        orLabel.className = 'or';
                        orLabel.innerText = 'or';
                        form.appendChild(orLabel);

                        const createExampleButton =
                            document.createElement('button');
                        createExampleButton.type = 'button';
                        createExampleButton.className = 'action-button';
                        createExampleButton.innerText =
                            "Initialize Example Project";
                        createExampleButton
                            .addEventListener('click', () => {
                                vscode.postMessage({tag:'CreateExample'});
                            });
                        form.appendChild(createExampleButton);

                        break;
                    case 'ScaffoldForm':
                        const { data } = state;
                        break;
                }
            }
        })()
    </script>
</body>
</html>`;
}
