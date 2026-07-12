/********************************************************************************
 * Copyright (c) 2026 Cinco Cloud.
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
import { DEFAULT_SERVER_PORT, DEFAULT_WEBSOCKET_PATH } from '@cinco-glsp/cinco-glsp-common';
import { PathMapping } from '@cinco-glsp/cinco-glsp-client';
import { getParameters } from '../url-parameters';

/**
 * Build the WebSocket URL to the GLSP server.
 *
 * Resolution order (first match wins):
 *
 * 1. **`?wsUrl=<full-url>`** — explicit full WebSocket URL override.
 *    Example: `?wsUrl=wss://my-server.example.com/glsp`
 *    Use this when you need complete control (e.g. a reverse proxy with a custom path).
 *
 * 2. **`?wsHost=<hostname>`** — override only the WebSocket host.
 *    The protocol, port, and path are derived from the other settings.
 *    Example: `?wsHost=my-server.example.com`.
 *
 * 3. **`?host=<hostname>`** — same as `wsHost`, kept for backwards compatibility.
 *    (`wsHost` is a better naming convention than `host`. `host` can be taken as deprecated)
 *
 * 4. **`?pathMapping=<path>`** — append a path segment after the host.
 *    Example: `?pathMapping=glsp` → `wss://host/glsp/<endpoint_id>`
 *
 * 5. **Default** — `wss://<hostname>:<port>/<endpoint_id>` (or `ws://` for HTTP).
 *
 * The WebSocket port defaults to `DEFAULT_SERVER_PORT` (5007) but can be overridden
 * with `?port=<port>`.
 *
 * E.g for cloud IDEs with subdomain-based port forwarding (e.g. GitHub Codespaces),
 * use `?wsHost=<derived-hostname>` explicitly, e.g.:
 *   `?wsHost=name-5007.app.github.dev`
 */
export function buildWebSocketUrl(): string {
    const params = getParameters();

    // 1. Full URL override
    if (params['wsUrl']) {
        return params['wsUrl'];
    }

    // 2/3. Host override (wsHost takes precedence, then host for backwards compat)
    const overrideHost = params['wsHost'] ?? params['host'];
    const effectiveHost = overrideHost
        ?? (window.location.hostname && window.location.hostname.length > 0 ? window.location.hostname : 'localhost');

    const wsProtocol = window.location.protocol && ['https', 'https:'].includes(window.location.protocol) ? 'wss' : 'ws';
    const endpointId = DEFAULT_WEBSOCKET_PATH;

    // 4. Path mapping
    if (PathMapping) {
        return `${wsProtocol}://${effectiveHost}/${PathMapping}/${endpointId}`;
    }

    // 5. Default: host:port
    const explicitPort = params['port'];
    const noPort = params['noPort'] === 'true';

    if (explicitPort) {
        return `${wsProtocol}://${effectiveHost}:${explicitPort}/${endpointId}`;
    } else if (noPort) {
        // no port specified
        return `${wsProtocol}://${effectiveHost}/${endpointId}`;
    } else {
        // append default port
        return `${wsProtocol}://${effectiveHost}:${DEFAULT_SERVER_PORT}/${endpointId}`;
    }
}
