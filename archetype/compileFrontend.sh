#!/bin/bash

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
cd ../;

# copy frontend
mkdir app/src/main/resources/META-INF;
mkdir app/src/main/resources/META-INF/resources;
mkdir app/src/main/resources/META-INF/resources/packages;
cp -r webapp/build/web/* app/src/main/resources/META-INF/resources;
rm -r app/src/main/resources/META-INF/resources/packages;
cp -r webapp/build/packages/ app/src/main/resources/META-INF/resources/packages/;
