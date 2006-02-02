#!/bin/sh

mvn clean

mvn assembly:assembly -DdescriptorId=jar-with-dependencies
mvn assembly:assembly -DdescriptorId=src
#mvn assembly:assembly -Ddescriptor=assembly.xml

mvn site

cp src/main/resources/wizcryptmsg.properties target/site/

