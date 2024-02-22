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
import { injectable } from '@theia/core/shared/inversify';
import { ReactWidget, codicon } from '@theia/core/lib/browser';
import * as React from 'react';

@injectable()
export class CincoProjectInitializerWidget extends ReactWidget {
    static readonly ID = 'cincoCloudProjectInitializer';
    static readonly LABEL = 'Cinco Cloud Project Initializer';

    constructor() {
        super();
        this.id = CincoProjectInitializerWidget.ID;
        this.title.label = CincoProjectInitializerWidget.LABEL;
        this.title.caption = CincoProjectInitializerWidget.LABEL;
        this.title.closable = true;
        this.title.iconClass = codicon('lightbulb-sparkle');
        this.update();
    }

    protected render(): React.JSX.Element {
        return (
            <CincoProjectInitializerView></CincoProjectInitializerView>
        );
    }
}

export class CincoProjectInitializerView extends React.Component<object, object> {
    constructor(props: object) {
        super(props);
    }

    override render(): React.JSX.Element {
        return (
            <div className="cinco-project-initializer">
                <h1>Welcome to Cinco Cloud</h1>
                <p>Click below to start your project</p>
                <button id="cinco-project-initializer-button">Start</button>
            </div>
        );
    }
}
