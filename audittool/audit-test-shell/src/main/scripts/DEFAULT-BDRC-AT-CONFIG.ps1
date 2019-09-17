#!/usr/bin/env bash
#
# Defaults for configuration
# default to everything here. These variables are also in config, if it exists
#
# Note this is intended to be called inline from auditTool.config, which defines
# $DEF_HOME
#
$rel = "0.9"
$ver = "SNAPSHOT-1"

# Audit test shell home
$CONFIG_ATHOME = $DEF_HOME
#
# Test library home
$CONFIG_TEST_LIB_JAR_FILE = Join-Path -Path $DEF_HOME -ChildPath audit-test-lib-$rel-$ver-jar-with-dependencies.jar
$CONFIG_SHELL_JAR_FILE = Join-Path -Path $DEF_HOME -ChildPath audit-test-shell-$rel-$ver.jar