/* eslint-disable header/header */
import { CommandContribution, CommandRegistry } from '@theia/core/lib/common';
import { inject } from '@theia/core/shared/inversify';
import { WorkspaceService } from '@theia/workspace/lib/browser';
import { FileDialogService, OpenFileDialogProps } from '@theia/filesystem/lib/browser';
import { injectable } from 'inversify';

import { initializeProjectCommand, openFilePickerCommand } from './initializeProjectCommand';
import URI from '@theia/core/lib/common/uri';

@injectable()
export class CommandRegistrationContribution implements CommandContribution {

    @inject(WorkspaceService)
    protected readonly workspaceService: WorkspaceService;

    @inject(FileDialogService)
    protected readonly fileDialogService!: FileDialogService;

    registerCommands(commands: CommandRegistry): void {
        commands.registerCommand(initializeProjectCommand, {
            execute: () => {
                commands.executeCommand(initializeProjectCommand.triggers).catch(() => {
                    alert('Opening project initialization dialog failed!');
                });
            }
        });
        commands.registerCommand(openFilePickerCommand, {
            isEnabled: () => true,
            isVisible: () => true,
            execute: ({ title, canSelectFolders, canSelectFiles, canSelectMany, filters }) =>
                this.openFilePickerDialog(title, canSelectFolders, canSelectFiles, canSelectMany, filters)
        });
    }

    async openFilePickerDialog(title: string, canSelectFolders: boolean, canSelectFiles: boolean, canSelectMany: boolean, filters: any): Promise<URI | undefined> {
        const props: OpenFileDialogProps = {
            title: title,
            canSelectFolders: canSelectFolders,
            canSelectFiles: canSelectFiles,
            canSelectMany: canSelectMany,
            filters: filters
        };
        // Get the first workspace root.
        const root = this.workspaceService.tryGetRoots()[0];
        // Open the `Open Dialog` with the given properties, and with respect to the given root.
        return this.fileDialogService.showOpenDialog(props, root);
    }
}
