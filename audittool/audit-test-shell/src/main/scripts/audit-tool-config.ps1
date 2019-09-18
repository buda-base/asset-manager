#
# Set-PSDebug -Trace 1
#
# Tool to configure audittool.ps1 installation
#
# Where am I?
$DEF_HOME = $PSScriptRoot
#
# VY important: make shell script directory working dir
# Understood in Linux, must be explicit in Win
[Environment]::CurrentDirectory = $DEF_HOME

# Change as needed
$rel = "SNAPSHOT-1"
$ver = "0.9"

# Load config, or defaults. this must be the same path as audittool.sh uses
$CONFIG = Join-Path -Path $env:HOMEPATH -ChildPath .config/bdrc/auditTool/config.ps1
$DEF_CONFIG =  Join-Path -Path $DEF_HOME -ChildPath "DEFAULT-BDRC-AT-CONFIG.ps1"


echo "This script prompts you for two files and saves the answers in '$CONFIG'."
echo ""
echo "First, it asks for the location of the test library. This allows you to update"
echo "the tests without re-installing everything."
echo ""
echo "Second, it asks for the jar file which runs the whole process."
echo "You press [Enter] to accept the defaults."
$okely = Read-Host "Press [Enter] when you are ready to continue."

$cfgdir = ( Split-Path -Path  $CONFIG -Parent )
If ( -not (Test-Path -Path  $cfgdir ))
{
    MD $cfgdir > $null
    echo ...Creating $cfgdir
}


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
    echo "Warning: no defaults file found. you must provide input for each prompt"
}

$TEST_TEST_JAR = Read-Host "Enter the path to the jar file which contains the tests [ default "$CONFIG_TEST_LIB_JAR_FILE "]?"

if (    [System.String]::IsNullOrEmpty( $TEST_TEST_JAR) -and
        [System.String]::IsNullOrEmpty($CONFIG_TEST_LIB_JAR_FILE))
{
    echo "No default found and no input given. Cannot continue."
    exit 1
}

# Resolve any relative dirs in input
if ( [System.String]::IsNullOrEmpty( $TEST_TEST_JAR))
{
    $TEST_TEST_JAR = $CONFIG_TEST_LIB_JAR_FILE
}
#
# Finally, set the value
# Depends on working dir being set.
$CONFIG_TEST_LIB_JAR_FILE = [System.IO.Path]::GetFullPath($TEST_TEST_JAR)

if ( -not [System.IO.File]::Exists($CONFIG_TEST_LIB_JAR_FILE))
{
    echo "Warning: test jar file "$CONFIG_TEST_LIB_JAR_FILE " does not exist (yet) "
}
# -----------------------------------
# Get the shell location
# ------------------------------------

$TEST_SHELL_JAR = Read-Host "Enter the path to the jar file which launches the process [ default "$CONFIG_SHELL_JAR_FILE\"]?"
if (    [System.String]::IsNullOrEmpty( $TEST_SHELL_JAR) -and
        [System.String]::IsNullOrEmpty($CONFIG_SHELL_JAR_FILE))
{
    echo "No default found and no input given. Cannot continue."
    exit 1
}

# Resolve any relative dirs in input
if ( [System.String]::IsNullOrEmpty( $TEST_SHELL_JAR))
{
    $TEST_SHELL_JAR = $CONFIG_SHELL_JAR_FILE
}
#
# And finally, set
$CONFIG_SHELL_JAR_FILE=[System.IO.Path]::GetFullPath($TEST_SHELL_JAR)

#
# Same process with the shell jar file
if ( -not [System.IO.File]::Exists($CONFIG_SHELL_JAR_FILE))
{
    echo "Warning: test jar file "$CONFIG_SHELL_JAR_FILE " does not exist (yet) "
}

##  BASH Shell

# And call the activation directory the directory of the
# Shell jar
#
$CONFIG_ATHOME =  (Split-Path -Path  $CONFIG_SHELL_JAR_FILE -Parent)
# Backup
if ( [System.IO.File]::Exists($CONFIG))
{
    $cb = (${CONFIG} + ".bak")
    echo ("--- " + (Get-Date -Format "yyyy-MM-dd") + "---") >> $cb
    cat $CONFIG >> $cb
}
#
#
# Write config
#
# This literal quoting is tricky
#
echo ('$CONFIG_ATHOME = "' + ${CONFIG_ATHOME} +  '" ') > $CONFIG
echo ('$CONFIG_TEST_LIB_JAR_FILE = "' + ${CONFIG_TEST_LIB_JAR_FILE} +  '"') >> $CONFIG
echo ( '$CONFIG_SHELL_JAR_FILE = "' + ${CONFIG_SHELL_JAR_FILE}  + '"') >> $CONFIG

