# Where am I?

$DEF_HOME = $PSScriptRoot


# Change as needed
$rel = "SNAPSHOT-1"
$ver = "0.9"

# Load config, or defaults. this must be the same path as audittool.sh uses
$CONFIG = Join-Path -Path $env:HOMEPATH -ChildPath .config/bdrc/auditTool/config.ps1
$DEF_CONFIG =  Join-Path -Path $DEF_HOME -ChildPath "DEFAULT-BDRC-AT-CONFIG.ps1"


# Must be the same path that auditTool-config.sh writes to
$CONFIG = Join-Path -Path $env:HOMEPATH -ChildPath .config/bdrc/auditTool/config.ps1

if ( [System.IO.File]::Exists($CONFIG))
{
    . $CONFIG
}
elseif ( [System.IO.File]::Exists($DEF_CONFIG))
{
    . $DEF_CONFIG
}

else
{
    echo "Configuration and default configurations are missing. Please contact BDRC for support."
}

#
# You can create other logging options in other files and define them here
$LOG_PROPS = Join-Path -Path $CONFIG_ATHOME -ChildPath log4j2.properties
#
# This is the system itself. Do not change it
$shellJar = $CONFIG_SHELL_JAR_FILE

# Unless you're goin to hack it
# $shellJar = "C:\Users\jsk\dev\asset-manager\audittool\audit-test-shell\target\output\audit-test-shell-0.9-SNAPSHOT-1.jar"
# no -Djava.security.manager "-Djava.security.policy=.\test.policy -DtestJar=$CONFIG_TEST_LIB_JAR_FILE" "-Dlog4j.configurationFile=$LOG_PROPS"
# java  "-DtestJar=$CONFIG_TEST_LIB_JAR_FILE -Dlog4j.configurationFile=$LOG_PROPS" -jar $shellJar $args
java "-Djava.security.manager -Djava.security.policy=${CONFIG_ATHOME}\audit-tool.policy "  "-DtestJar=$CONFIG_TEST_LIB_JAR_FILE" "-Dlog4j.configurationFile=$LOG_PROPS" -jar $shellJar $args
# Try without jar, and without interface jar in current dir
# nope - PS C:\tmp> C:\users\jsk\Desktop\adt9\audittool.ps1trello
# Error: Could not find or load main class io.bdrc.am.audit.shell.shell
# java -classpath C:\Users\jsk\dev\asset-manager\audittool\audit-test-shell\target\output\audit-test-interface-0.9-SNAPSHOT-1.jar "-DtestJar=$CONFIG_TEST_LIB_JAR_FILE" "-Dlog4j.configurationFile=$LOG_PROPS" io.bdrc.am.audit.shell.shell $args
# -classpath C:\Users\jsk\dev\asset-manager\audittool\audit-test-shell\target\output\audit-test-interface-0.9-SNAPSHOT-1.jar

#pushd  C:\Users\jsk\dev\asset-manager\audittool\audit-test-shell\target\output\
#java  "-DtestJar=$CONFIG_TEST_LIB_JAR_FILE" "-Dlog4j.configurationFile=$LOG_PROPS" -jar $shellJar $args
#popd
