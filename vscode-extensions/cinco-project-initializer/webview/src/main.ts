import { Command, ScaffoldData } from "extension/src/common-types"
import * as java from './java'

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
    validation: ScaffoldFormValidation
}

interface ScaffoldFormValidation {
    modelNameValid: boolean
    packageNameValid: boolean
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
                    validation: {
                        modelNameValid: true,
                        packageNameValid: true,
                    }
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
            const { data, validation } = state;

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

            const modelNameInputBox = document.createElement('div');
            modelNameInputBox.className = 'form-input-box';
            modelNameLabel.appendChild(modelNameInputBox);

            const modelNameInput = document.createElement('input');
            modelNameInput.type = 'text';
            modelNameInput.value = data.modelName;
            modelNameInput.addEventListener('input', (event: any) => {
                data.modelName = event.target.value;
                validation.modelNameValid =
                    java.isValidIdentifier(data.modelName);
                modelNameValidationDisplay.style.display =
                    validation.modelNameValid ? 'none' : 'block';
                modelNameInput.classList
                    .toggle('error', !validation.modelNameValid);
                updateSubmitButtonEnabledState();
                vscode.setState(state);
            });
            modelNameInputBox.appendChild(modelNameInput);

            const modelNameValidationDisplay = document.createElement('p');
            modelNameValidationDisplay.className = 'validation-display';
            modelNameValidationDisplay.innerText =
                'Not a valid Java identifier.';
            modelNameValidationDisplay.style.display = 'none';
            modelNameInputBox.appendChild(modelNameValidationDisplay);

            const packageNameLabel = document.createElement('label');
            packageNameLabel.className = 'text-input-label';
            form.appendChild(packageNameLabel);

            const packageNameLegend = document.createElement('span');
            packageNameLegend.className = 'text-input-legend';
            packageNameLegend.innerText = 'Package Name';
            packageNameLabel.appendChild(packageNameLegend);

            const packageNameInputBox = document.createElement('div');
            packageNameInputBox.className = 'form-input-box';
            packageNameLabel.appendChild(packageNameInputBox);

            const packageNameInput = document.createElement('input');
            packageNameInput.type = 'text';
            packageNameInput.value = data.packageName;
            packageNameInput.addEventListener('input', (event: any) => {
                data.packageName = event.target.value;
                validation.packageNameValid =
                    java.isValidPackageIdentifier(data.packageName);
                packageNameValidationDisplay.style.display =
                    validation.packageNameValid ? 'none' : 'block';
                packageNameInput.classList
                    .toggle('error', !validation.packageNameValid);
                updateSubmitButtonEnabledState();
                vscode.setState(state);
            });
            packageNameInputBox.appendChild(packageNameInput);

            const packageNameValidationDisplay = document.createElement('p');
            packageNameValidationDisplay.className = 'validation-display';
            packageNameValidationDisplay.innerText =
                'Not a valid Java package identifier.';
            packageNameValidationDisplay.style.display = 'none';
            packageNameInputBox.appendChild(packageNameValidationDisplay);

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

            function updateSubmitButtonEnabledState() {
                initializeProjectButton.disabled =
                    !validation.modelNameValid || !validation.packageNameValid;
            }

            break;
        }
    }
}
