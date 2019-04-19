#!/usr/bin/env bash
#
# wrapper shell for audittool
# Use maven artifacts for now
# Change as needed
rel=SNAPSHOT
ver=1.0

# Where am I? Configure this for your environment
export ATHOME='<<#$CONFIG-ATHOME>>'
#
# This is the test library. Add your version here.
libJar=target/audit-test-lib-${ver}-${rel}-jar-with-dependencies.jar

#
export LOG_PROPS=${ATHOME}/log4j2.properties
#
# This is the system itself. Do not change it
shellJar=${ATHOME}/audit-test-shell-${ver}-${rel}.jar

# shellJar was built to point to its own mainclass
# see audit-test-lib/pom.xml
java  -DtestJar=${libJar} -DatHome=${ATHOME} -Dlog4j.configurationFile=${LOG_PROPS} -jar ${shellJar} $@