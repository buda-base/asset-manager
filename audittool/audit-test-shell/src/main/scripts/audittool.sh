#!/usr/bin/env bash
#
# If no config, everything is in this directory
DEF_HOME=$(dirname $(readlink -m  $0))

DEF_CONFIG=DEFAULT-BDRC-AT-CONFIG.sh

# Must be the same path that auditTool-config.sh writes to
CONFIG=${HOME}/.config/bdrc/auditTool/config

if [[  -f ${CONFIG} ]] ; then
    . ${CONFIG}
else if [[ -f ${DEF_HOME}/${DEF_CONFIG} ]] ; then
    . ${DEF_HOME}/${DEF_CONFIG}
else
    echo "Configuration and default configurations are missing. Please contact BDRC for support."
fi
fi


#
export LOG_PROPS=${CONFIG_ATHOME}/log4j2.properties
#
# This is the system itself. Do not change it
shellJar=${CONFIG_SHELL_JAR_FILE}

# shellJar was built to point to its own mainclass
java  -DtestJar=${CONFIG_TEST_LIB_JAR_FILE} -DatHome=${CONFIG_ATHOME} -Dlog4j.configurationFile=${LOG_PROPS} -jar ${shellJar} $@