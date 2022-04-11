#!/usr/bin/env bash
exec docker run --env-file ./env.list --add-host=host.docker.internal:host-gateway -p 0.0.0.0:3000:3000 -p 0.0.0.0:8000:8000 --rm -it editor
