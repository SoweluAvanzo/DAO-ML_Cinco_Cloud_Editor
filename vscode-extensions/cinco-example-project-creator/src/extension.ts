import { commands, ExtensionContext, OutputChannel, window } from 'vscode';
import { createExample } from './example/exampleCreator';

export let workbenchOutput: OutputChannel;
export let extensionContext: ExtensionContext
export const commandId = "info.scce.cinco-cloud.example-project.create";
export function activate(context: ExtensionContext) {
	extensionContext = context;
	workbenchOutput = window.createOutputChannel("Example Project Creator");
	// Generator
    const generateCommand = commands.registerCommand(commandId, (workspaceFsPath: string) => createExample(workspaceFsPath));
    context.subscriptions.push(generateCommand);
}
