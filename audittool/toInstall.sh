#!/usr/bin/env bash
VER=${1:-"0.8"}
REL=${2:-"SNAPSHOT-2"}

cp audit-test-lib/target/audit-test-lib-${VER}-${REL}-jar-with-dependencies.jar install
cp audit-test-shell/target/audit-test-shell-${VER}-${REL}-*.zip install
