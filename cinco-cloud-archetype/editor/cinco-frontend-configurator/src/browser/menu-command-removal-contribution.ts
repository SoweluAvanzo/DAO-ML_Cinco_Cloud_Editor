/* eslint-disable header/header */
import { FileService } from '@theia/filesystem/lib/browser/file-service';
import { CommandRegistry, MenuContribution, MenuModelRegistry } from '@theia/core';
import { FrontendApplication, FrontendApplicationContribution } from '@theia/core/lib/browser';
import { inject } from '@theia/core/shared/inversify';
import { WorkspaceCommands } from '@theia/workspace/lib/browser';
import { injectable } from 'inversify';

export let cmdRegistry: CommandRegistry;
export let fileService: FileService;

@injectable()
export class MenuCommandRemovalContribution implements MenuContribution, FrontendApplicationContribution {

    @inject(CommandRegistry)
    protected readonly commandRegistry: CommandRegistry;

    @inject(FileService)
    private readonly fileService: FileService;

    onStart?(app: FrontendApplication): void {
        cmdRegistry = this.commandRegistry;
        fileService = this.fileService;
        this.unregisterCommands(this.commandRegistry);
    }

    public unregisterCommands(registry: CommandRegistry): void {
        // unregister all commands, that could allow access to filesystem
        registry.unregisterCommand(WorkspaceCommands.OPEN);
        registry.unregisterCommand(WorkspaceCommands.OPEN_FILE);
        registry.unregisterCommand(WorkspaceCommands.OPEN_FOLDER);
        registry.unregisterCommand(WorkspaceCommands.OPEN_WORKSPACE);
        registry.unregisterCommand(WorkspaceCommands.OPEN_RECENT_WORKSPACE);
        registry.unregisterCommand(WorkspaceCommands.SAVE_WORKSPACE_AS);
        registry.unregisterCommand(WorkspaceCommands.CLOSE);
        registry.unregisterCommand(WorkspaceCommands.OPEN_WORKSPACE_FILE);
        registry.unregisterCommand(WorkspaceCommands.ADD_FOLDER);
        registry.unregisterCommand(WorkspaceCommands.REMOVE_FOLDER);
        registry.unregisterCommand('workbench.action.newWindow');
        registry.unregisterCommand('workbench.action.addRootFolder');
        registry.unregisterCommand('workbench.action.openFile');
        registry.unregisterCommand('workbench.action.openFileFolder');
        registry.unregisterCommand('workbench.action.openFileFolder');
    }

    public registerMenus(registry: MenuModelRegistry): void {

        // unregister all menu-commands, that could allow access to filesystem
        registry.unregisterMenuAction(WorkspaceCommands.OPEN);
        registry.unregisterMenuAction(WorkspaceCommands.OPEN_FILE);
        registry.unregisterMenuAction(WorkspaceCommands.OPEN_FOLDER);
        registry.unregisterMenuAction(WorkspaceCommands.OPEN_WORKSPACE);
        registry.unregisterMenuAction(WorkspaceCommands.OPEN_RECENT_WORKSPACE);
        registry.unregisterMenuAction(WorkspaceCommands.SAVE_WORKSPACE_AS);
        registry.unregisterMenuAction(WorkspaceCommands.CLOSE);
        registry.unregisterMenuAction(WorkspaceCommands.OPEN_WORKSPACE_FILE);
        registry.unregisterMenuAction(WorkspaceCommands.SAVE_AS);
        registry.unregisterMenuAction('workbench.action.newWindow');
    }
}

