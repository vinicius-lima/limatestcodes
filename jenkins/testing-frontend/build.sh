#!/bin/bash

#TIMESTAMP=$(date +%s)
#FILE_NAME="testing-frontend-${TIMESTAMP}.tar.gz"

FILE_NAME="testing-frontend.tar.gz"

cd src/
tar -czf ../$FILE_NAME *
cd -
mv $FILE_NAME dist/$FILE_NAME
