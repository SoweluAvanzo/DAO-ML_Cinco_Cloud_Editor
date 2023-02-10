export type MessageToServer = CreateScaffold | CreateExample | ConfirmQuestion

interface CreateScaffold {
    tag: 'CreateScaffold'
    data: ScaffoldData
}

interface CreateExample {
    tag: 'CreateExample'
}

export interface ScaffoldData {
    modelName: string
    packageName: string
}

export type MessageToClient = ServerError | ServerConfirm

interface ServerError {
    tag: 'ServerError'
    error: string
}

interface ServerConfirm{
    tag: 'ServerConfirm'
    message: string
    options: string[]
    purpose: MessagePurpose
}

export interface ConfirmQuestion{
    tag: 'ConfirmQuestion'
    answer: string
    purpose: MessagePurpose
}

type MessagePurpose = 'ClearWorkspace'


