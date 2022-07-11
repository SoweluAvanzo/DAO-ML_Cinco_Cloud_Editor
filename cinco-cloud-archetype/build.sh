#!/usr/bin/env bash

# copy language-server sources
cp -r ../cinco-language-server/ cinco-language-server/
# remove emf-workspace
rm -r cinco-language-server/workspace-emf-tmp

docker build -t editor .
