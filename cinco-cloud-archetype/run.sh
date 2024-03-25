#!/usr/bin/env bash
exec docker run \
    --name cinco-cloud-archetype \
    --env-file ./env.list \
    --add-host=host.docker.internal:host-gateway \
    --volume=./editor/workspace:/editor/workspace \
    -p 3000:3000 \
    -p 3003:3003 \
    -p 5007:5007 \
    --rm \
    -it \
    editor
