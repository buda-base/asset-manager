#!/usr/bin/env bash
#
# Defaults for configuration
# default to everything here. These variables are also in config, if it exists
#
rel=0.9
ver=SNAPSHOT-1

# Audit test shell home
export CONFIG_ATHOME=${DEF_HOME}
#
# Test library home
export CONFIG_TEST_LIB_JAR_FILE=${DEF_HOME}/audit-test-lib-${rel}-${ver}-jar-with-dependencies.jar
export CONFIG_SHELL_JAR_FILE=${DEF_HOME}/audit-test-shell-${rel}-${ver}.jar