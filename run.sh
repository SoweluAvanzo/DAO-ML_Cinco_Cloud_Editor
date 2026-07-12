#!/usr/bin/env bash
set -e

# Clean up any existing container with the same name
docker rm -f dao_ml_editor 2>/dev/null || true

# Run the editor container.
# Use -d (detached) so the container stays alive after the script exits.
docker run \
    --name dao_ml_editor \
    --env-file ./env.list \
    --add-host=host.docker.internal:host-gateway \
    --volume=./workspace:/editor/workspace \
    --volume=./languages:/editor/languages \
    -p 3000:3000 \
    -p 3003:3003 \
    -p 5007:5007 \
    --rm \
    -d \
    editor

echo "Container started. Editor available at port 3003 (diagram) and 3000 (Theia)"

# Detect the environment and construct the correct URL.
# Cloud dev environments typically expose ports via subdomains rather than :port.
if [ -n "$CODESPACE_NAME" ]; then
    # Suffix pattern: <name>-<port>.<domain>
    BASE_URL="https://${CODESPACE_NAME}-3003.app.github.dev"
    EDITOR_URL="https://${CODESPACE_NAME}-3000.app.github.dev"
elif [ -n "$GITPOD_WORKSPACE_ID" ] && [ -n "$GITPOD_WORKSPACE_URL" ]; then
    # Prefix pattern: <port>-<workspace>.<domain>
    _DOMAIN=$(echo "$GITPOD_WORKSPACE_URL" | sed 's|https://||; s|http://||')
    BASE_URL="https://3003-${_DOMAIN}"
    EDITOR_URL="https://3000-${_DOMAIN}"
else
    # Local: localhost
    BASE_URL="http://localhost:3003"
    EDITOR_URL="http://localhost:3000"
fi

echo "  Diagram e.g.: ${BASE_URL}/diagram.html?model=WIRE.dao"
echo "  Landing: ${BASE_URL}/"
echo "  Editor: ${EDITOR_URL}/#/editor/workspace"
echo ""

# Wait for the server to start inside the container
echo "Waiting for server to initialize..."
for i in $(seq 1 30); do
    if curl -s -o /dev/null http://localhost:3003/ 2>/dev/null; then
        echo "Server is ready."
        break
    fi
    sleep 1
done

# Refresh port-forwarding tunnels in cloud environments where the proxy
# can get stuck returning 502. Toggling visibility forces re-establishment.
if [ -n "$CODESPACE_NAME" ]; then
    echo "Refreshing port tunnels..."
    for port in 3000 3003 5007; do
        gh codespace ports visibility ${port}:private -c "$CODESPACE_NAME" 2>/dev/null || true
        sleep 1
        gh codespace ports visibility ${port}:public -c "$CODESPACE_NAME" 2>/dev/null || true
        sleep 1
    done
    echo "Port tunnels refreshed."
elif [ -n "$GITPOD_WORKSPACE_ID" ]; then
    echo "Cloud environment detected. Ports are auto-forwarded."
    echo "If the WebSocket doesn't connect, ensure port 5007 is public in the Ports panel."
fi

echo ""
echo "Use 'docker logs -f dao_ml_editor' to view server logs."
echo "Use 'docker rm -f dao_ml_editor' to stop."