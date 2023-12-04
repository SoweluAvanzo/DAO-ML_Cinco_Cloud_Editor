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
import { Attribute, CustomType } from '@cinco-glsp/cinco-glsp-common/lib/meta-specification';
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
    currentCustomTypeDefinitions: CustomType[] = [];
    currentValues: any = {};
    dataSubscriptions: (() => void)[] = [];

    @postConstruct()
    postConstruct(): void {
        this.shell.onDidChangeActiveWidget(change => {
            if (change.newValue instanceof CincoGLSPDiagramWidget) {
                this.currentFileUri = change.newValue.getResourceUri()?.path.fsPath() ?? '';
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
        customTypeDefinitions?: CustomType[],
        values?: any
    ): void {
        this.currentModelElementId = modelElementId ?? '';
        this.currentModelType = modelType ?? this.currentModelType;
        this.currentModelElementIndex = modelElementIndex ?? this.currentModelElementIndex;
        this.currentAttributeDefinitions = attributeDefinitions ?? this.currentAttributeDefinitions;
        this.currentCustomTypeDefinitions = customTypeDefinitions ?? this.currentCustomTypeDefinitions;
        this.currentValues = values ?? this.currentValues;
        this.dataSubscriptions.forEach(fn => fn());
    }

    reset(): void {
        this.currentFileUri = '';
        this.currentModelElementIndex = {};
        this.currentModelType = '';
        this.currentModelElementId = '';
        this.currentAttributeDefinitions = [];
        this.currentCustomTypeDefinitions = [];
        this.currentValues = {};
    }

    registerDataSubscription(callback: () => void): void {
        this.dataSubscriptions.push(callback);
    }
}
