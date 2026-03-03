#!/usr/bin/env bash
# Clean up any existing container with the same name
docker rm -f dao_ml_editor 2>/dev/null || true

# Run the editor
exec docker run \
    --name dao_ml_editor \
    --env-file ./env.list \
    --add-host=host.docker.internal:host-gateway \
    --volume=./workspace:/editor/workspace \
    --volume=./languages:/editor/languages \
    -p 3000:3000 \
    -p 3003:3003 \
    -p 5007:5007 \
    --rm \
    -it \
    editor
