export type Command = CreateScaffold | CreateExample

interface CreateScaffold {
    tag: "CreateScaffold"
    data: ScaffoldData
}

interface CreateExample {
    tag: "CreateExample"
}

export type WebviewState = Initial | ScaffoldForm

interface Initial {
    tag: "Initial"
}

interface ScaffoldForm {
    tag: "ScaffoldForm"
    data: ScaffoldData
}

export interface ScaffoldData {
    modelName: string
    packageName: string
}
