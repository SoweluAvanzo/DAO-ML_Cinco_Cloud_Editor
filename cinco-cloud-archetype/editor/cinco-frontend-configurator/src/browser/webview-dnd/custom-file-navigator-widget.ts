/* eslint-disable header/header */

import { ContextMenuRenderer } from '@theia/core/lib/browser/context-menu-renderer';
import { TreeProps, SelectableTreeNode } from '@theia/core/lib/browser/tree';
import { inject, Container, interfaces } from '@theia/core/shared/inversify';
import { URI } from '@theia/core/lib/common/uri';

import { FileNode, createFileTreeContainer } from '@theia/filesystem/lib/browser';

import { FileNavigatorCommands } from '@theia/navigator/lib/browser/navigator-contribution';
import { FileNavigatorWidget } from '@theia/navigator/lib/browser/navigator-widget';
import { FileNavigatorTree } from '@theia/navigator/lib/browser/navigator-tree';
import { FileNavigatorModel } from '@theia/navigator/lib/browser/navigator-model';
import { NavigatorDecoratorService } from '@theia/navigator/lib/browser/navigator-decorator-service';
import { FILE_NAVIGATOR_PROPS } from '@theia/navigator/lib/browser/navigator-container';

import { currentView } from './drag-and-drop-handler';
import { isWebview } from './drag-and-drop-definitions';

export class CustomFileNavigatorWidget extends FileNavigatorWidget {

    public static onWebviewDnD: Function | undefined = undefined;

    constructor(@inject(TreeProps) props: TreeProps,
        @inject(FileNavigatorModel) override readonly model: FileNavigatorModel,
        @inject(ContextMenuRenderer) contextMenuRenderer: ContextMenuRenderer
    ) {
        super(props, model, contextMenuRenderer);
    }

    protected enableDndOnMainPanel(): void {
        const mainPanelNode = this.shell.mainPanel.node;
        this.addEventListener(mainPanelNode, 'drop', async ({ dataTransfer }) => {
            const treeNodes = dataTransfer && this.getSelectedTreeNodesFromData(dataTransfer) || [];
            if (treeNodes.length > 0) {
                treeNodes.filter(FileNode.is).forEach(treeNode => {
                    if (!SelectableTreeNode.isSelected(treeNode)) {
                        this.model.toggleNode(treeNode);
                    }
                });
                // prevent opening-file if the target view is a webview
                if (!isWebview(currentView)) {
                    this.commandService.executeCommand(FileNavigatorCommands.OPEN.id);
                } else if (CustomFileNavigatorWidget.onWebviewDnD) { // do something new defined when drop on webview
                    CustomFileNavigatorWidget.onWebviewDnD();
                }
            } else if (dataTransfer && dataTransfer.files?.length > 0) {
                // the files were dragged from the outside the workspace
                Array.from(dataTransfer.files).forEach(async file => {
                    const fileUri = new URI((file as any).path);
                    const opener = await this.openerService.getOpener(fileUri);
                    opener.open(fileUri);
                });
            }
        });
        const handler = (e: DragEvent): any => {
            if (e.dataTransfer) {
                e.dataTransfer.dropEffect = 'link';
                e.preventDefault();
            }
        };
        this.addEventListener(mainPanelNode, 'dragover', handler);
        this.addEventListener(mainPanelNode, 'dragenter', handler);
    }
}

export function createFileNavigatorWidget(parent: interfaces.Container): CustomFileNavigatorWidget {
    return createCustomFileNavigatorContainer(parent).get(CustomFileNavigatorWidget);
}

export function createCustomFileNavigatorContainer(parent: interfaces.Container): Container {
    const child = createFileTreeContainer(parent, {
        tree: FileNavigatorTree,
        model: FileNavigatorModel,
        widget: CustomFileNavigatorWidget,
        decoratorService: NavigatorDecoratorService,
        props: FILE_NAVIGATOR_PROPS
    });
    return child;
}
