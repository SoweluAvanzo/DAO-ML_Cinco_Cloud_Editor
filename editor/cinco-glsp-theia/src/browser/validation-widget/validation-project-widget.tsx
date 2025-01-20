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
import '../../../css/validation-widget.css';

import { ValidationMessage } from '@cinco-glsp/cinco-glsp-common/lib/protocol/validation-protocol';
import { codicon, ReactWidget } from '@theia/core/lib/browser';
import { injectable } from 'inversify';
import React = require('react');

import { CincoCloudModelValidationWidget } from './validation-model-widget';

@injectable()
export class CincoCloudProjectValidationWidget extends ReactWidget {
    static readonly ID = 'cincoCloudProjectValidationView';
    static readonly LABEL = 'Cinco Cloud Project Validation';

    override readonly id = CincoCloudProjectValidationWidget.ID;
    readonly label = CincoCloudProjectValidationWidget.LABEL;

    state: {
        models: {
            validationMessages: ValidationMessage[];
            toggled: boolean;
            modelName: string;
        }[];
    } = {
        models: [
            /*
            {
                validationMessages: [
                    {
                        status: ValidationStatus.Info,
                        name: 'Test Info',
                        message: 'Lorem ipsum'
                    },
                    {
                        status: ValidationStatus.Warning,
                        name: 'Warning Info',
                        message: 'Lorem ipsum'
                    },
                    {
                        status: ValidationStatus.Pass,
                        name: 'Pass Info',
                        message: 'Lorem ipsum'
                    },
                    {
                        status: ValidationStatus.Error,
                        name: 'Error Info',
                        message: 'Lorem ipsum'
                    }
                ],
                toggled: false,
                modelName: 'Test Model Name'
            },
            {
                validationMessages: [
                    {
                        status: ValidationStatus.Info,
                        name: 'Test Info',
                        message: 'Lorem ipsum'
                    },
                    {
                        status: ValidationStatus.Warning,
                        name: 'Warning Info',
                        message: 'Lorem ipsum'
                    },
                    {
                        status: ValidationStatus.Pass,
                        name: 'Pass Info',
                        message: 'Lorem ipsum'
                    }
                ],
                toggled: false,
                modelName: 'Another Test Model Name'
            }
            */
        ]
    };

    constructor() {
        super();
        this.title.label = CincoCloudProjectValidationWidget.LABEL;
        this.title.caption = CincoCloudProjectValidationWidget.LABEL;
        this.title.iconClass = codicon('check-all');
        this.title.closable = true;
        this.update();
    }

    toggleValidationMessages(index: number): void {
        this.state.models[index].toggled = !this.state.models[index].toggled;
        this.update();
    }

    protected render(): React.ReactNode {
        const validationEntries = this.state.models.map((model, index) => (
            <div className='validation-entry' key={'validation-entry-' + index}>
                <span
                    className={model.toggled ? codicon('chevron-down') : codicon('chevron-right')}
                    onClick={() => this.toggleValidationMessages(index)}
                ></span>
                <span
                    className={CincoCloudModelValidationWidget.getStatusIconClass(
                        CincoCloudModelValidationWidget.getOverallStatus(model.validationMessages)
                    )}
                ></span>
                <span>{model.modelName}</span>
                {model.toggled && (
                    <div className='validation-messages'>
                        {model.validationMessages.map((message, innerIndex) => (
                            <div className='validation-entry' key={'validation-message-' + index + '-' + innerIndex}>
                                <span className={CincoCloudModelValidationWidget.getStatusIconClass(message.status)}></span> {message.name}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        ));
        return <div className='validation-widget'>{validationEntries ?? 'No models could be detected in your workspace.'}</div>;
    }
}
