#!/bin/sh
#----------------------------------------------------
# This script puts together the entire districution
#----------------------------------------------------
version=1.0.4

while getopts v:d: flag
do
    case "${flag}" in
	v) version=${OPTARG};;
	d) distribute=true;;
    esac
done

echo "version : ${version}";
echo "distribute : ${distribute}";

#----------------------------------------------------
# Create directory structure
#----------------------------------------------------
echo 'cleanning up'
rm -rf j4np-${version}
rm -rf j4np-${version}.tar.gz
mkdir -p  j4np-${version}/lib/core
mkdir -p  j4np-${version}/lib/ext
#----------------------------------------------------
# Copy Jars and directories to the distribution
#----------------------------------------------------
echo 'copying jars and exacutable'
cp -r modules/j4np-package/bin j4np-${version}/.
cp -r modules/j4np-package/etc j4np-${version}/.
cp modules/j4np-package/target/*with-dependencies.jar j4np-${version}/lib/core/j4np-${version}.jar
#cp extensions/j4np-analysis/target/*with-dependencies.jar j4np-${version}/lib/ext/.
cp extensions/j4ml-track/target/*with-dependencies.jar j4np-${version}/lib/ext/.
#cp extensions/j4ml-classifier/target/*with-dependencies.jar j4np-${version}/lib/ext/.
#cp extensions/j4ml-classifier/target/*with-depend*.jar j4np-${version}/lib/ext/.
#cp extensions/j4ml-display/target/*with-dependencies.jar j4np-${version}/lib/ext/.
#----------------------------------------------------
# package it, and distribute it if the flag is set
#----------------------------------------------------
tar -cf j4np-${version}.tar j4np-${version}
gzip j4np-${version}.tar
#----------------------------------------------------
# Copy local distribution
#----------------------------------------------------
echo 'copying local distribution to : ' ${PROJECT}
cp -r j4np-${version} $PROJECT/.
echo 'done....'
jput -r j4np-${version}.tar.gz
echo 'done copying to jlab'

