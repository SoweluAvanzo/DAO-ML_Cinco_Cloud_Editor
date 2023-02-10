import {MessageToClient, MessageToServer} from '../common/model'

export const dataInvalid:MessageToClient = {
    tag: 'ServerError',
    error: 'Cannot initialize project, input data is invalid.',
}

export const clearWorkspace:MessageToClient = {
    tag: 'ServerConfirm',
    message: 'Cannot create example project, workspace is not empty. Clear Directory',
    options: ['Yes','No'],
    purpose: 'ClearWorkspace'

};