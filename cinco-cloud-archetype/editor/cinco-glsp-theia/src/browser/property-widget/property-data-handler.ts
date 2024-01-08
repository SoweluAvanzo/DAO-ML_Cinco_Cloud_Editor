/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
import {
    Attribute,
    getFileExtension,
    getGraphModelOfFileType,
    isDiagramExtension
} from '@cinco-glsp/cinco-glsp-common/lib/meta-specification';
import { ModelElementIndex } from '@cinco-glsp/cinco-glsp-common/lib/protocol/property-model';
import { SelectionService } from '@theia/core';
import { inject, injectable, postConstruct } from 'inversify';

@injectable()
export class PropertyDataHandler {
    @inject(SelectionService) selectionService: SelectionService;
    selection: any;

    // state
    currentFileUri = '';
    currentModelElementIndex: ModelElementIndex = {};
    currentModelType = '';
    currentModelElementId = '';
    currentAttributeDefinitions: Attribute[] = [];
    currentValues: any = {};
    dataSubscriptions: (() => void)[] = [];

    @postConstruct()
    postConstruct(): void {
        const updateSelection = (selection: any): void => {
            // parse current selection
            this.selection = selection;
            let selectedGraphType: string | undefined;
            if (this.selection) {
                const sourceUri = this.selection['sourceUri'];
                if (sourceUri) {
                    const fileType = getFileExtension(sourceUri);
                    const graphType = getGraphModelOfFileType(fileType);
                    selectedGraphType = graphType?.elementTypeId;
                }
            }
            // update
            this.updatePropertySelection(
                this.currentModelElementIndex,
                selectedGraphType ?? this.currentModelType,
                this.currentModelElementId,
                this.currentAttributeDefinitions,
                this.currentValues
            );
        };
        this.selectionService.onSelectionChanged(updateSelection);
    }

    updatePropertySelection(
        modelElementIndex: ModelElementIndex,
        modelType: string,
        modelElementId: string,
        attributeDefinitions: Attribute[],
        values: any
    ): void {
        // check if canvas is shown
        const sourceUri = this.selection?.['sourceUri'] ?? '';
        if (this.selection && isDiagramExtension(sourceUri, modelType)) {
            if (this.currentFileUri !== sourceUri) {
                this.reset();
                this.currentFileUri = sourceUri;
            } else {
                this.currentModelElementId = this.getContainedSelection(this.selection, modelType, modelElementIndex, modelElementId) ?? '';
                this.currentModelElementIndex = modelElementIndex;
                this.currentModelType = modelType;
                this.currentAttributeDefinitions = attributeDefinitions;
                this.currentValues = values;
            }
        } else {
            this.reset();
        }
        this.dataSubscriptions.forEach(fn => fn());
    }

    getContainedSelection(
        selection: any | undefined,
        modelType: string,
        modelElementIndex: ModelElementIndex,
        fallback: string | undefined
    ): string | undefined {
        const selectedElements = selection['selectedElementsIDs'];
        if (selectedElements === undefined) {
            return undefined;
        }
        if (selectedElements.length <= 0) {
            return this.getModelId(modelType, modelElementIndex);
        }
        if (selectedElements) {
            for (const [, modelElements] of Object.entries(modelElementIndex)) {
                for (const element of modelElements) {
                    for (const selectedElement of selectedElements) {
                        if (element.id === selectedElement) {
                            return selectedElement;
                        }
                    }
                }
            }
        }
        return fallback;
    }

    getModelId(modelType: string, modelElementIndex: ModelElementIndex): string | undefined {
        for (const [, modelElements] of Object.entries(modelElementIndex)) {
            for (const element of modelElements) {
                if (element.elementTypeId === modelType) {
                    return element.id;
                }
            }
        }
        return undefined;
    }

    reset(): void {
        this.currentFileUri = '';
        this.currentModelElementIndex = {};
        this.currentModelType = '';
        this.currentModelElementId = '';
        this.currentAttributeDefinitions = [];
        this.currentValues = {};
    }

    registerDataSubscription(callback: () => void): void {
        this.dataSubscriptions.push(callback);
    }
}
