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

export type MessageToClient = ServerError | ServerConfirm | Notification

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

export interface Notification{
    tag: 'Notification'
    message: string
    level: NotificationLevel
}

type NotificationLevel = 'Information' | 'Warning' | 'Error'


type MessagePurpose = 'ClearWorkspace'


