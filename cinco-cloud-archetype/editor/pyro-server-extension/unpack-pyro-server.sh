#!/bin/bash

PYRO_SUBPATH="$1"
cd /editor/pyro-server-extension
unzip pyro-server-binaries.zip
cd pyro-server/
jar xf ./app.jar ./META-INF/resources/index.html
baseHref=$(echo "${PYRO_SUBPATH}" | sed "s/\//\\\\\//g")
sed -i "s/base href=\"\/\"/base href=\"${baseHref}\"/g" ./META-INF/resources/index.html
jar uf ./app.jar ./META-INF/resources/index.html
echo "Set baseHref to: ${PYRO_SUBPATH}
