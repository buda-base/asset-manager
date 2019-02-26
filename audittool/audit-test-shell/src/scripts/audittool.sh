#!/usr/bin/env bash
#
# wrapper shell for audittool
# Use maven artifacts for now
# Change as needed
rel=SNAPSHOT
ver=1.0


libJar=audit-test-lib.1.0-SNAPSHOT.jar

java -cp ${libJar}:audit-test-interface-${ver}-${rel}.jar:audit-test-interface-${ver}-${rel}.jar io.bdrc.am.main.shell.shell -p $*