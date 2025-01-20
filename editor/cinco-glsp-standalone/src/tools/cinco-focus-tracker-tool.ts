/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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

import { FocusTrackerTool } from '@eclipse-glsp/client';
import { injectable } from 'inversify';

@injectable()
export class CincoFocusTrackerTool extends FocusTrackerTool {
    disabled = true;

    protected override async focusIn(event: FocusEvent): Promise<void> {
        let message: string | undefined;
        const target = event.target;

        if (target instanceof HTMLElement) {
            const parent = this.parentWithAriaLabel(target);
            const textMessage = this.handleTextNode(target);
            // eslint-disable-next-line no-null/no-null
            if (target.ariaLabel !== null) {
                message = this.handleAriaLabel(target);
            } else {
                if (parent === undefined && textMessage !== undefined) {
                    message = textMessage;
                } else if (parent !== undefined && textMessage === undefined) {
                    message = `Focus is in ${parent.ariaLabel}`;
                } else if (parent !== undefined && textMessage !== undefined) {
                    message = `${parent.ariaLabel} -> ${textMessage}`;
                }
            }
        } else if (target instanceof SVGElement) {
            const textMessage = this.svgHandleTextNode(target);
            // eslint-disable-next-line no-null/no-null
            if (target.ariaLabel !== null) {
                message = this.svgHandleAriaLabel(target);
            } else {
                if (textMessage !== undefined) {
                    message = textMessage;
                } else if (textMessage === undefined) {
                    message = `Focus is in ${target.dataset.svgMetadataType}`;
                }
            }
        }
        if (!this.disabled) {
            await this.showToast(message);
        }
    }

    protected override async focusOut(event: FocusEvent): Promise<void> {
        if (!this.disabled) {
            await this.showToast('Focus not set');
        }
    }

    protected svgHandleTextNode(target: SVGElement): string | undefined {
        const textNode = Array.prototype.filter
            .call(target.childNodes, element => element.nodeType === Node.TEXT_NODE)
            .map(element => element.textContent)
            .join('');

        if (textNode.trim().length !== 0) {
            return textNode;
        }

        return undefined;
    }

    protected svgHandleAriaLabel(target: SVGElement): string | undefined {
        // eslint-disable-next-line no-null/no-null
        return target.ariaLabel === null ? undefined : target.ariaLabel;
    }
}
