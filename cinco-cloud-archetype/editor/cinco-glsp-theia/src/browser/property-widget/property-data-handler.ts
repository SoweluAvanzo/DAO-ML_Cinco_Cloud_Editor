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
import { Attribute } from '@cinco-glsp/cinco-glsp-common/lib/meta-specification';
import { ModelElementIndex } from '@cinco-glsp/cinco-glsp-common/lib/protocol/property-model';
import { ApplicationShell } from '@theia/core/lib/browser';
import { inject, injectable, postConstruct } from 'inversify';
import { CincoGLSPDiagramWidget } from '../diagram/cinco-glsp-diagram-widget';

@injectable()
export class PropertyDataHandler {
    @inject(ApplicationShell) shell: ApplicationShell;

    // state
    currentFileUri = '';
    currentModelElementIndex: ModelElementIndex = {};
    currentModelType = '';
    currentModelElementId = '';
    currentAttributeDefinitions: Attribute[] = [];
    currentValues: any = {};
    dataSubscriptions: (() => void)[] = [];
    currentWidget?: CincoGLSPDiagramWidget;

    @postConstruct()
    postConstruct(): void {
        this.shell.onDidChangeActiveWidget(change => {
            const newWidget = change.newValue;
            if (newWidget instanceof CincoGLSPDiagramWidget) {
                const newFileUri = (
                    newWidget instanceof CincoGLSPDiagramWidget ?
                        newWidget.getResourceUri()?.path.fsPath()
                        : this.currentFileUri)
                        ?? '';
                if(newFileUri !== this.currentFileUri) { // update Widget
                    this.currentWidget = newWidget;
                    this.currentFileUri = newFileUri;
                    this.dataSubscriptions.forEach(fn => fn());
                }
            }
        });
        this.shell.onDidRemoveWidget(widget => {
            console.log('Closed WIdget'+widget.toString());
            if(widget === this.currentWidget) {
                this.reset();
                this.dataSubscriptions.forEach(fn => fn());
            }
        });
    }

    updatePropertySelection(
        modelElementId: string,
        modelType?: string,
        modelElementIndex?: ModelElementIndex,
        attributeDefinitions?: Attribute[],
        values?: any
    ): void {
        this.currentModelElementId = modelElementId ?? '';
        this.currentModelType = modelType ?? this.currentModelType;
        this.currentModelElementIndex = modelElementIndex ?? this.currentModelElementIndex;
        this.currentAttributeDefinitions = attributeDefinitions ?? this.currentAttributeDefinitions;
        this.currentValues = values ?? this.currentValues;
        this.dataSubscriptions.forEach(fn => fn());
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
