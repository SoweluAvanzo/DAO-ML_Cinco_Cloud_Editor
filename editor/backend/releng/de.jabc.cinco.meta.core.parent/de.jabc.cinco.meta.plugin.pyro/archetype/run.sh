#!/bin/bash

cd /usr/src/app;
pub get;
pub global activate webdev;
webdev serve --release --hostname=0.0.0.0;
