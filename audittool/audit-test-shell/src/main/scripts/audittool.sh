#!/usr/bin/env bash
#


# If no config, everything is in this directory
DEF_HOME=$(dirname $0)


CONFIG=${HOME}/.bdrc/audit-tool-config

if [[  -f ${CONFIG} ]] ; then
    . ${CONFIG}
else if [[ -f ${DEF_HOME}/DEFAULT-CONFIG ]] ; then
    . ${DEF_HOME}/DEFAULT-CONFIG ]
else
    echo "Configuration and default configurations are missing. Please contact BDRC for support.

fi


#
export LOG_PROPS=${CONFIG_ATHOME}/log4j2.properties
#
# This is the system itself. Do not change it
shellJar=${CONFIG_ATHOME}/CONFIG_SHELL_JAR_FILE

# shellJar was built to point to its own mainclass
java  -DtestJar=${CONFIG_TEST_LIB_JAR_FILE} -DatHome=${CONFIG_ATHOME} -Dlog4j.configurationFile=${LOG_PROPS} -jar ${shellJar} $@