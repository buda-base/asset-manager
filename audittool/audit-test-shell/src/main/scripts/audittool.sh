#!/usr/bin/env bash
#
# wrapper shell for audittool
# Use maven artifacts for now
# Change as needed
rel=SNAPSHOT
ver=1.0
#
# This is the test library. Add your version here.
libJar=target/audit-test-lib-${ver}-${rel}-jar-with-dependencies.jar


#
# This is the system itself. Do not change it
shellJar=audit-test-shell-${ver}-${rel}.jar

# shellJar was built to point to its own mainclass
# see audit-test-lib/pom.xml
java  -DtestJar=${libJar} -Dlog4j.configurationFile=log4j.properties -jar ${shellJar} $@