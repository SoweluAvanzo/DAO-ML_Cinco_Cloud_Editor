import { MessageToClient, MessageToServer, ScaffoldData } from "../common/model"
import * as java from '../common/java'

declare function acquireVsCodeApi(): VsCodeApi

interface VsCodeApi {
    getState(): WebviewState | undefined
    setState(state: WebviewState): void
    postMessage(message: MessageToServer): void
}

type WebviewState = Initial | ScaffoldForm | ConfirmationDialog
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

interface ConfirmationDialog {
    tag: "ConfirmationDialog"
    message: string
    options: string[]
    lastState: WebviewState
}

const vscode = acquireVsCodeApi();

const previousState = vscode.getState();
let state: WebviewState = previousState ? previousState : {tag:'Initial'};

const pageElement = document.createElement('div');
pageElement.className = 'page';
document.body.appendChild(pageElement);

const errorBoxDisplayDefault = 'flex';

const errorBox = document.createElement('div');
errorBox.className = 'error-box';
errorBox.style.display = 'none';
pageElement.appendChild(errorBox);

const errorDisplay = renderErrorBox();

function renderErrorBox() {
    errorBox.innerHTML = '';

    const errorDisplay = document.createElement('p');
    errorDisplay.className = 'error-display';
    errorBox.appendChild(errorDisplay);

    const errorCloseButton = document.createElement('button');
    errorCloseButton.type = 'button';
    errorCloseButton.className =
        'error-close-button button regular-button secondary-button';
    errorCloseButton.setAttribute('aria-label', 'Close');
    errorCloseButton.innerText = 'X';
    errorCloseButton.addEventListener('click', () => {
        errorBox.style.display = 'none';
        errorDisplay.innerText = '';
    });
    errorBox.appendChild(errorCloseButton);
    return errorDisplay;
}

function renderConfirmationDialog(currentstate: ConfirmationDialog){
    const errorOptions = document.createElement('div');
    errorOptions.innerText = currentstate.message;

    currentstate.options.forEach(function(option:string){
        const button = document.createElement('button');
        button.type = 'button';
        button.className =
        'button primary-button action-button';
    button.innerText = option;
    button.addEventListener('click', () => {
        let msg:MessageToServer = {
            tag: 'ConfirmQuestion',
            answer: option,
            purpose:'ClearWorkspace'
        };
        vscode.postMessage(msg);
        state = currentstate.lastState;
        vscode.setState(state);
        renderContent();
    });
    errorOptions.appendChild(document.createElement('p'))
    errorOptions.appendChild(button);

});
    return errorOptions;
}


const errorCloseButton = document.createElement('button');
errorCloseButton

const contentBox = document.createElement('div');
contentBox.className = 'content-box';
pageElement.appendChild(contentBox);

renderContent();

window.addEventListener('message', event => processIncomingMessage(event.data));

function processIncomingMessage(message: MessageToClient) {
    switch (message.tag) {
        case 'ServerError':
            errorBox.style.display = errorBoxDisplayDefault;
            errorDisplay.innerText = message.error;
        break;
        case 'ServerConfirm':
            switch(message.purpose){
                case "ClearWorkspace":
                    state = {
                        tag:'ConfirmationDialog',
                        message: message.message,
                        options: message.options,
                        lastState: state
                    };
                    vscode.setState(state);
                    renderContent();
                    break;
            }
            
        break;
    }
}

function renderContent() {
    contentBox.innerHTML = '';

    switch (state.tag) {
        case 'Initial': {
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
                renderContent();
                vscode.setState(state);
            });
            contentBox.appendChild(createScaffoldButton);

            const orText = document.createElement('p');
            orText.className = 'or';
            orText.innerText = 'or';
            contentBox.appendChild(orText);

            const createExampleButton =
                document.createElement('button');
            createExampleButton.type = 'button';
            createExampleButton.className =
                'button primary-button action-button';
            createExampleButton.innerText = "Initialize an Example Project";
            createExampleButton.addEventListener('click', () => {
                vscode.postMessage({tag:'CreateExample'});
            });
            contentBox.appendChild(createExampleButton);

            break;
        }
        case 'ScaffoldForm': {
            const { data, validation } = state;

            const form = document.createElement('form');
            form.addEventListener('submit', event => {
                event.preventDefault();
                vscode.postMessage({
                    tag: 'CreateScaffold',
                    data
                });
            });
            contentBox.appendChild(form);

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
                renderContent();
                vscode.setState(state);
            });
            buttonBox.appendChild(backButton);

            const initializeProjectButton = document.createElement('button');
            initializeProjectButton.type = 'submit';
            initializeProjectButton.className =
                'button regular-button primary-button';
            initializeProjectButton.innerText = 'Initialize Project';
            buttonBox.appendChild(initializeProjectButton);

            function updateSubmitButtonEnabledState() {
                initializeProjectButton.disabled =
                    !validation.modelNameValid || !validation.packageNameValid;
            }

            break;


        }


        case 'ConfirmationDialog':{
            contentBox.appendChild(renderConfirmationDialog(state));    
            break;
        }
    
    }
}
