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


export const projectInitialized:MessageToClient = {
    tag: 'Notification',
    message: 'Project has been initialized.',
    level: 'Information'
}

export const workspaceCleared:MessageToClient = {
    tag: 'Notification',
    message: 'Project cleared.',
    level: 'Information'
}