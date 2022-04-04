export type MessageToServer = CreateScaffold | CreateExample

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

export type MessageToClient = ServerError

interface ServerError {
    tag: 'ServerError'
    error: string
}
