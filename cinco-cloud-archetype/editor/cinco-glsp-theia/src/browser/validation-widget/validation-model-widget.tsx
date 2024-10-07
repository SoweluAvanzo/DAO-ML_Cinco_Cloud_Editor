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

import { ValidationMessage, ValidationStatus } from '@cinco-glsp/cinco-glsp-common/lib/protocol/validation-protocol';
import { codicon, ReactWidget } from '@theia/core/lib/browser';
import { inject, injectable, postConstruct } from 'inversify';
import React = require('react');

import { ValidationModelDataHandler } from './validation-model-data-handler';

@injectable()
export class CincoCloudModelValidationWidget extends ReactWidget {
    static readonly ID = 'cincoCloudModelValidationView';
    static readonly LABEL = 'Cinco Cloud Model Validation';

    @inject(ValidationModelDataHandler) validationModelDataHandler: ValidationModelDataHandler;

    override readonly id = CincoCloudModelValidationWidget.ID;
    readonly label = CincoCloudModelValidationWidget.LABEL;

    state: {
        validationMessages: ValidationMessage[];
        toggled: boolean;
    } = {
        validationMessages: [
            /*
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
            */
        ],
        toggled: false
    };

    constructor() {
        super();
        this.title.label = CincoCloudModelValidationWidget.LABEL;
        this.title.caption = CincoCloudModelValidationWidget.LABEL;
        this.title.iconClass = codicon('check');
        this.title.closable = true;
        this.update();
    }

    @postConstruct()
    registerDataSubscription(): void {
        this.validationModelDataHandler.registerDataSubscription(() => {
            this.state.validationMessages = Array.from(this.validationModelDataHandler.currentMessages.values()).flat();
            this.update();
        });
        this.update();
    }

    static getStatusIconClass(status: ValidationStatus): string {
        let classString = 'validation-icon ';
        switch (status) {
            case ValidationStatus.Error:
                classString += 'error ' + codicon('error');
                break;
            case ValidationStatus.Info:
                classString += 'info ' + codicon('info');
                break;
            case ValidationStatus.Pass:
                classString += 'pass ' + codicon('pass');
                break;
            case ValidationStatus.Warning:
                classString += 'warning ' + codicon('warning');
                break;
        }
        return classString;
    }

    static getOverallStatus(messages: ValidationMessage[]): ValidationStatus {
        let mostCriticalStatus = ValidationStatus.Pass;
        messages.forEach(message => {
            mostCriticalStatus = message.status > mostCriticalStatus ? message.status : mostCriticalStatus;
        });
        return mostCriticalStatus;
    }

    toggleValidationMessages(): void {
        this.state.toggled = !this.state.toggled;
        this.update();
    }

    protected render(): React.ReactNode {
        const validationMessages = this.state.validationMessages.map((message, index) => (
            <div className='validation-entry' key={'validation-message-' + index}>
                <span className={CincoCloudModelValidationWidget.getStatusIconClass(message.status)}></span> {message.name} -{' '}
                {message.message}
            </div>
        ));
        return (
            <div className='validation-widget'>
                <div className='validation-entry'>
                    <span
                        className={this.state.toggled ? codicon('chevron-down') : codicon('chevron-right')}
                        onClick={() => this.toggleValidationMessages()}
                    ></span>
                    <span
                        className={CincoCloudModelValidationWidget.getStatusIconClass(
                            CincoCloudModelValidationWidget.getOverallStatus(this.state.validationMessages)
                        )}
                    ></span>
                    <span>Model Validation</span>
                    {this.state.toggled && <div className='validation-messages'>{validationMessages}</div>}
                </div>
            </div>
        );
    }
}
