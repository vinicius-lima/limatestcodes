#!/bin/bash

PROJECT_NAME="aws-environment-management"
FILE_NAME="${PROJECT_NAME}.tar.gz"
DIST_PATH="./dist/${PROJECT_NAME}"

cd $DIST_PATH
tar -czf ../$FILE_NAME *
cd -
