#!/bin/sh

#####################################################
## This script is used to create a .bin executable ##
## for the *nix environments. Pass the version     ##
## number to be built as parameter. This script    ##
## requires that the build machine already have    ##
## an rpm build of WizCrypt installed.             ##
#####################################################

## Check if parameter version is given

if [ "$#" -ne 1 ];then
	echo Provide version number to be built as parameter.
	exit 1
fi

WC_VERSION=$1

## Check if shar command is present in the build environment

if ! `which shar > /dev/null 2>&1`
then
	echo Your environment does not have \`GNU shar\` installed
	exit 1
fi

## Check if RPM build is installed

if [ ! -e /usr/bin/wizcrypt -o ! -e /usr/share/java/WizCrypt-${WC_VERSION}-jar-with-dependencies.jar -o ! -d /usr/share/doc/WizCrypt-${WC_VERSION}/ ]
then
	echo RPM build of WizCrypt for version ${WC_VERSION} does not seem to be installed. Needed for generating .bin
	exit 1
fi

shar -zq /usr/bin/wizcrypt /usr/share/java/WizCrypt-${WC_VERSION}-jar-with-dependencies.jar /usr/share/man/man1/wizcrypt.1.gz /usr/share/doc/WizCrypt-${WC_VERSION}/ > target/WizCrypt-${WC_VERSION}.bin

## Post build check

SHAR_EXIT_STATUS=$?

if [ "${SHAR_EXIT_STATUS}" -eq 0 ];then
	echo "*** Successfully generated 'target/WizCrypt-${WC_VERSION}.bin' file! ***"
else
	echo shar failed!
	exit ${SHAR_EXIT_STATUS}
fi

