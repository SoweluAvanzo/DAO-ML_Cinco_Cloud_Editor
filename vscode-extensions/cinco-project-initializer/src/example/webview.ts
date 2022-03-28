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

        .wizard-form {
            width: 100%;
            max-width: 500px;
        }

        .action-button {
            display: block;
            width: 100%;
            padding: 16px;
        }

        .form-title {
            font-size: 20px;
        }

        .button-box {
            display: flex;
            justify-content: space-between;
            margin-top: 16px;
        }

        .button {
            border: none;
            cursor: pointer;
        }

        .regular-button {
            padding: 4px 9px;
        }

        .primary-button {
            background: var(--vscode-button-background);
            color: var(--vscode-button-foreground);
        }

        .primary-button:hover {
            background: var(--vscode-button-hoverBackground);
        }

        .secondary-button {
            background: var(--vscode-button-secondaryBackground);
            color: var(--vscode-button-secondaryForeground);
        }

        .secondary-button:hover {
            background: var(--vscode-button-secondaryHoverBackground);
        }

        .text-input-label {
            display: flex;
            align-items: center;
            margin: 8px 0;
        }

        .text-input-legend {
            width: 150px;
        }

        .form-input {
            flex: 1;
        }

        input[type="text"] {
            padding: 2px 4px;
            background: var(--vscode-input-background);
            color: var(--vscode-input-foreground);
            border: var(--vscode-input-border);
        }

        input[type="text"]:focus {
            background: var(--vscode-inputOption-activeBackground);
            color: var(--vscode-inputOption-activeForeground);
            border: var(--vscode-inputOption-activeBorder);
        }

        input[type="text"]::placeholder {
            color: var(--vscode-input-placeholderForeground);
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
                    case 'Initial': {
                        const form = document.createElement('form');
                        form.className = 'wizard-form';
                        document.body.appendChild(form);

                        const createScaffoldButton =
                            document.createElement('button');
                        createScaffoldButton.type = 'button';
                        createScaffoldButton.className =
                            'button primary-button action-button';
                        createScaffoldButton.innerText =
                            "Create a New Cinco Language…";
                        createScaffoldButton.addEventListener('click', () => {
                            state = {
                                tag: 'ScaffoldForm',
                                data: {
                                    modelName: 'SomeGraph',
                                    packageName: 'info.scce.cinco.product.somegraph',
                                },
                            };
                            renderPage();
                            vscode.setState(state);
                        });
                        form.appendChild(createScaffoldButton);

                        const orText = document.createElement('p');
                        orText.className = 'or';
                        orText.innerText = 'or';
                        form.appendChild(orText);

                        const createExampleButton =
                            document.createElement('button');
                        createExampleButton.type = 'button';
                        createExampleButton.className =
                            'button primary-button action-button';
                        createExampleButton.innerText =
                            "Initialize an Example Project";
                        createExampleButton.addEventListener('click', () => {
                            vscode.postMessage({tag:'CreateExample'});
                        });
                        form.appendChild(createExampleButton);

                        break;
                    }
                    case 'ScaffoldForm': {
                        const { data } = state;

                        const form = document.createElement('form');
                        form.className = 'wizard-form';
                        document.body.appendChild(form);

                        const title = document.createElement('p');
                        title.className = 'form-title';
                        title.innerText = "Create a New Cinco Language";
                        form.appendChild(title);

                        const modelNameLabel =
                            document.createElement('label');
                        modelNameLabel.className = 'text-input-label';
                        form.appendChild(modelNameLabel);

                        const modelNameLegend =
                            document.createElement('span');
                        modelNameLegend.className = 'text-input-legend';
                        modelNameLegend.innerText = 'Model Name';
                        modelNameLabel.appendChild(modelNameLegend);

                        const modelNameInput =
                            document.createElement('input');
                        modelNameInput.type = 'text';
                        modelNameInput.className = 'form-input';
                        modelNameInput.value = data.modelName;
                        modelNameInput.addEventListener('input', event => {
                            data.modelName = event.target.value;
                            vscode.setState(state);
                        });
                        modelNameLabel.appendChild(modelNameInput);

                        const packageNameLabel =
                            document.createElement('label');
                        packageNameLabel.className = 'text-input-label';
                        form.appendChild(packageNameLabel);

                        const packageNameLegend =
                            document.createElement('span');
                        packageNameLegend.className = 'text-input-legend';
                        packageNameLegend.innerText = 'Package Name';
                        packageNameLabel.appendChild(packageNameLegend);

                        const packageNameInput =
                            document.createElement('input');
                        packageNameInput.type = 'text';
                        packageNameInput.className = 'form-input';
                        packageNameInput.value = data.packageName;
                        packageNameInput.addEventListener('input', event => {
                            data.packageName = event.target.value;
                            vscode.setState(state);
                        });
                        packageNameLabel.appendChild(packageNameInput);

                        const buttonBox = document.createElement('div');
                        buttonBox.className = 'button-box';
                        form.appendChild(buttonBox);

                        const backButton = document.createElement('button');
                        backButton.type = 'button';
                        backButton.className =
                            'button regular-button secondary-button';
                        backButton.innerText = 'Back';
                        backButton.addEventListener('click', () => {
                            state = {tag:'Initial'};
                            renderPage();
                            vscode.setState(state);
                        });
                        buttonBox.appendChild(backButton);

                        const initializeProjectButton =
                            document.createElement('button');
                        initializeProjectButton.type = 'button';
                        initializeProjectButton.className =
                            'button regular-button primary-button';
                        initializeProjectButton.innerText =
                            'Initialize Project';
                        initializeProjectButton.addEventListener('click', () => {
                            vscode.postMessage({
                                tag: 'CreateScaffold',
                                data: state.data
                            });
                        });
                        buttonBox.appendChild(initializeProjectButton);

                        break;
                    }
                }
            }
        })()
    </script>
</body>
</html>`;
}
