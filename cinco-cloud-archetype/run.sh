#!/usr/bin/env bash
exec docker run \
    --name cinco-cloud-archetype \
    --env-file ./env.list \
    --add-host=host.docker.internal:host-gateway \
    --volume=./editor/workspace:/editor/workspace \
    -p 0.0.0.0:3000:3000 \
    -p 0.0.0.0:8000:8000 \
    --rm \
    -it \
    editor
