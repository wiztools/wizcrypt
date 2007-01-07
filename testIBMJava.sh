#!/bin/sh

export JAVA_HOME=/usr/java/ibm-java2-i386-60
export PATH=$JAVA_HOME/bin:$PATH

mvn test

if [ $? -ne 0 ]; then
	echo
	echo ERROR: Test Failed
	echo
fi
