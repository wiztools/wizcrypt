#!/bin/sh

mvn clean

mvn assembly:assembly -DdescriptorId=jar-with-dependencies
mvn assembly:assembly -DdescriptorId=src
#mvn assembly:assembly -Ddescriptor=assembly.xml

mvn site

cp src/main/resources/wizcryptmsg.properties target/site/

cp target/WizCrypt-1.0-jar-with-dependencies.jar target/site/
cp target/WizCrypt-1.0-src.tar.gz target/site/
cp target/WizCrypt-1.0-src.tar.bz2 target/site/
cp target/WizCrypt-1.0-src.zip target/site/

