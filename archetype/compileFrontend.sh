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