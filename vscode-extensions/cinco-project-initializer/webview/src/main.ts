import { Command, ScaffoldData } from "extension/src/common-types";

declare function acquireVsCodeApi(): VsCodeApi

interface VsCodeApi {
    getState(): WebviewState | undefined
    setState(state: WebviewState): void
    postMessage(message: Command): void
}

type WebviewState = Initial | ScaffoldForm

interface Initial {
    tag: "Initial"
}

interface ScaffoldForm {
    tag: "ScaffoldForm"
    data: ScaffoldData
}

const vscode = acquireVsCodeApi();

const previousState = vscode.getState();
let state: WebviewState = previousState ? previousState : {tag:'Initial'};

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
            createScaffoldButton.innerText = "Create a New Cinco Language…";
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
            createExampleButton.innerText = "Initialize an Example Project";
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

            const modelNameLabel = document.createElement('label');
            modelNameLabel.className = 'text-input-label';
            form.appendChild(modelNameLabel);

            const modelNameLegend = document.createElement('span');
            modelNameLegend.className = 'text-input-legend';
            modelNameLegend.innerText = 'Model Name';
            modelNameLabel.appendChild(modelNameLegend);

            const modelNameInput = document.createElement('input');
            modelNameInput.type = 'text';
            modelNameInput.className = 'form-input';
            modelNameInput.value = data.modelName;
            modelNameInput.addEventListener('input', (event: any) => {
                data.modelName = event.target.value;
                vscode.setState(state);
            });
            modelNameLabel.appendChild(modelNameInput);

            const packageNameLabel = document.createElement('label');
            packageNameLabel.className = 'text-input-label';
            form.appendChild(packageNameLabel);

            const packageNameLegend = document.createElement('span');
            packageNameLegend.className = 'text-input-legend';
            packageNameLegend.innerText = 'Package Name';
            packageNameLabel.appendChild(packageNameLegend);

            const packageNameInput = document.createElement('input');
            packageNameInput.type = 'text';
            packageNameInput.className = 'form-input';
            packageNameInput.value = data.packageName;
            packageNameInput.addEventListener('input', (event: any) => {
                data.packageName = event.target.value;
                vscode.setState(state);
            });
            packageNameLabel.appendChild(packageNameInput);

            const buttonBox = document.createElement('div');
            buttonBox.className = 'button-box';
            form.appendChild(buttonBox);

            const backButton = document.createElement('button');
            backButton.type = 'button';
            backButton.className = 'button regular-button secondary-button';
            backButton.innerText = 'Back';
            backButton.addEventListener('click', () => {
                state = {tag:'Initial'};
                renderPage();
                vscode.setState(state);
            });
            buttonBox.appendChild(backButton);

            const initializeProjectButton = document.createElement('button');
            initializeProjectButton.type = 'button';
            initializeProjectButton.className =
                'button regular-button primary-button';
            initializeProjectButton.innerText = 'Initialize Project';
            initializeProjectButton.addEventListener('click', () => {
                vscode.postMessage({
                    tag: 'CreateScaffold',
                    data
                });
            });
            buttonBox.appendChild(initializeProjectButton);

            break;
        }
    }
}
