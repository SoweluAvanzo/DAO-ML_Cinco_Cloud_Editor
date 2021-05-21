#!/bin/bash
set -e

echo "$(date +"[%T.%3N]") Evaluate Options... "
clean="false"
buildBackend='false'
buildFrontend='false'
forceFrontend='false'
runFrontend='false'

if [[ "$1" == "" ]]; then
  clean='true'
  buildBackend='true'
  buildFrontend='true'
  runFrontend='true'
fi

if [[ ${#1} -gt 2 ]]; then

  if [[ "$1" == -*"c"* ]]; then
    clean='true'
  fi
  if [[ "$1" == -*"b"* ]]; then
    buildBackend='true'
  fi

  if [[ "$1" == -*"f"* ]]; then
    buildFrontend='true'
  fi
  if [[ "$1" == -*"r"* ]]; then
    runFrontend='true'
  fi
  if [[ "$1" == -*"ff"* ]]; then
    forceFrontend='true'
  fi
fi

while [ "$1" != "" ]; do
  case $1 in
    -c | --clean )  clean='true'
                      ;;
    -b | --backend )  buildBackend='true'
                      ;;
    -f | --frontend ) buildFrontend='true'
                      ;;
    -ff | --forcefrontend ) forceFrontend='true'
                      ;;
    -r | --run )      runFrontend='true'
                      ;;
  esac
  shift
done

[[ "$clean" == "true" ]] && echo "  Clean (-c)" || echo "  Do not clean (-c)"
[[ "$buildBackend" == "true" ]] && echo "  Build Backend (-b)" || echo "  Do not build Backend (-b)"
[[ "$forceFrontend" == "true" ]] && echo "  Remove yarn.lock (-ff)" || echo "  Do not remove yarn.lock  (-ff)"
[[ "$buildFrontend" == "true" ]] && echo "  Build Frontend (-f)" || echo "  Do not build Frontend (-f)"
[[ "$runFrontend" == "true" ]] && echo "  Run Frontend (-r)" || echo "  Do not run Frontend (-r)"

if [ "$clean" == "true" ]; then
  echo "$(date +"[%T.%3N]") cleaning folders"
  rm -rf cinco-extension/language-server && \
	rm -rf cinco-extension/node_modules && \
	rm -rf cinco-extension/package-lock.json && \
	rm -rf cinco-extension/out && \
	rm -rf web/node_modules && \
	rm -rf web/browser-app/lib && \
	rm -rf web/browser-app/node_modules && \
	rm -rf web/browser-app/plugins && \
	rm -rf web/browser-app/src-gen && \
	rm -rf web/browser-app/gen-webpack.config.js && \
	rm -rf web/browser-app/webpack.config.js && \
	rm -rf backend/releng/de.jabc.cinco.meta.core.parent/workspace-emf-tmp && \
  rm -rf backend/releng/de.jabc.cinco.meta.core.parent/language-server
fi

if [ "$buildBackend" == "true" ]; then
  echo "$(date +"[%T.%3N]") Build backend products"
  cd backend/releng/de.jabc.cinco.meta.core.parent/
  mvn clean install
  cd ../../../
  cp -r backend/releng/de.jabc.cinco.meta.core.parent/language-server cinco-extension
fi

if [ "$forceFrontend" == "true" ]; then
  cd web/
  rm -f ./yarn.lock
  cd ..
fi

if [ "$buildFrontend" == "true" ]; then
  cd cinco-extension/
  yarn
  cd ..
  mkdir web/browser-app/plugins && \
  cp cinco-extension/cinco-extension-0.0.1.vsix web/browser-app/plugins/cinco-extension-0.0.1.vsix
  cd web/
  yarn
  cd ..
fi

if [ "$runFrontend" == "true" ]; then
  workspace=$(pwd)
  (sleep 5 && x-www-browser http://127.1:3000/#/${workspace:1}/backend/examples/Example)&
  cd web/browser-app
  yarn start --hostname 0.0.0.0
fi

