export type Command = CreateScaffold | CreateExample

interface CreateScaffold {
    tag: "CreateScaffold"
    data: ScaffoldData
}

interface CreateExample {
    tag: "CreateExample"
}

export interface ScaffoldData {
    modelName: string
    packageName: string
}
