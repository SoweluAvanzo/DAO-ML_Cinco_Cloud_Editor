#!/usr/bin/env bash

# copy language-server sources
cp -r ../cinco-language-server/ cinco-ls/
# remove emf-workspace
rm -r cinco-ls/workspace-emf-tmp

docker build -t editor .
