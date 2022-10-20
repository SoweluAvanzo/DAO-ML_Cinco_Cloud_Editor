#!/bin/bash

# status code of the last command
status=0;

# build frontend
cd webapp;
if [[ "$OSTYPE" == "win32" ]]; then
    pub.bat global activate webdev \
        && pub.bat get \
        && pub.bat run build_runner build -o build;
elif [[ "$OSTYPE" == "msys" ]]; then
    pub.bat global activate webdev \
        && pub.bat get \
        && pub.bat run build_runner build -o build;
else
    pub global activate webdev \
        && pub get \
        && pub run build_runner build -o build;
fi
status=$?
if [[ $status -ne 0 ]]; then
    exit $status;
fi

cd ../;

# copy frontend
mkdir -p app/src/main/resources/META-INF/resources/packages;
cp -r webapp/build/web/* app/src/main/resources/META-INF/resources;
rm -r app/src/main/resources/META-INF/resources/packages;
cp -r webapp/build/packages/ app/src/main/resources/META-INF/resources/packages/;