# Where am I?

$DEF_HOME = $PSScriptRoot


# Change as needed
$rel = "SNAPSHOT-1"
$ver = "0.9"

# Load config, or defaults. this must be the same path as audittool.sh uses
$CONFIG = Join-Path -Path $home-ChildPath .config/bdrc/auditTool/config.ps1
$DEF_CONFIG =  Join-Path -Path $DEF_HOME -ChildPath "DEFAULT-BDRC-AT-CONFIG.ps1"

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

#
# or -DatHome=${CONFIG_ATHOME} in shell
$env:ATHOME = $CONFIG_ATHOME

#
# Windows uses embedded test jar
java "-Dlog4j.configurationFile=$LOG_PROPS" -jar $shellJar $args

