#!/bin/sh

##############################################
## Test script to perform the logo.png Test ##
## Details in src/test/resources/README     ##
##############################################

T=`ls target/wizcrypt-*-jar-with-dependencies.jar`
P_DIR=`pwd`
EXEC=${P_DIR}/${T}

PASSWD=password

## 1. Encrypt

cp src/test/resources/logo.png /tmp/
cd /tmp/
java -jar ${EXEC} -e -p ${PASSWD} logo.png
md5sum logo.png.wiz > logo.png.wiz.md5

DIFF_OUT=`diff logo.png.wiz.md5 ${P_DIR}/src/test/resources/logo.png.wiz.md5`
if [ -z "${DIFF_OUT}" ]; then
  echo Encryption creates same output!
else
  echo Encryption fails!
  exit 1
fi

## 2. Decrypt

java -jar ${EXEC} -d -p ${PASSWD} logo.png.wiz
md5sum logo.png > logo.png.md5

DIFF_OUT=`diff logo.png.md5 ${P_DIR}/src/test/resources/logo.png.md5`
if [ -z "${DIFF_OUT}" ]; then
  echo Decryption creates same output!
else
  echo Decryption fails!
  exit 2
fi

