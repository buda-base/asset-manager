#!/usr/bin/env bash
#
# Tool to configure audittool.sh installation
#
# Where am I?
DEF_HOME=$(dirname $0)
# wrapper shell for audittool
# Use maven artifacts for now
# Change as needed
export rel=SNAPSHOT-2
export ver=0.8

# Load config, or defaults. this must be the same path as audittool.sh uses
CONFIG=${HOME}/.config/bdrc/auditTool/config
DEF_CONFIG=DEFAULT-BDRC-AT-CONFIG.sh


echo "This script prompts you for a file path and saves the answers in '$CONFIG'."
echo ""
echo "It asks for the jar file which runs the whole process."
echo "You press [Enter] to accept the defaults."
# shellcheck disable=SC2034
read -p "Press [Enter] when you are ready to continue." okely


configDir="$(dirname ${CONFIG})"
[[ -d "${configDir}" ]] || { mkdir -p "${configDir}" ; }

if [[  -f ${CONFIG} ]] ; then
    # shellcheck disable=SC1090
    # shellcheck disable=SC2086
    . ${CONFIG}
elif [[ -f ${DEF_HOME}/${DEF_CONFIG} ]] ; then
      # shellcheck disable=SC1090
    # shellcheck disable=SC1090
    . ${DEF_HOME}/${DEF_CONFIG}
else
    echo "Warning: no defaults file found. you must provide input for each prompt"
fi


 #
 #
 # Get the shell location
 #

 read -p "Enter the path to the jar file which launches the process [ default \"${CONFIG_SHELL_JAR_FILE}\" ]?" TEST_SHELL

 [[ -z ${TEST_SHELL} ]] && [[ -z ${CONFIG_SHELL_JAR_FILE} ]]  && { echo "No default found and no input given. Cannot continue." ; exit 1; }

 CONFIG_SHELL_JAR_FILE=$(readlink -m ${TEST_SHELL:-${CONFIG_SHELL_JAR_FILE}})

[[ -f ${CONFIG_SHELL_JAR_FILE} ]] || { echo "Warning: shell jar file \"${CONFIG_SHELL_JAR_FILE}\"does not exist (yet) " ; }

 #
 # And call the activation directory the directory of the 
 # Shell jar
 #
 CONFIG_ATHOME=$(dirname ${CONFIG_SHELL_JAR_FILE})
 # Backup
 # shellcheck disable=SC2046
 [[ -f ${CONFIG}  ]] && { echo "--- " $(date) "---" >> ${CONFIG}.bak ; cat ${CONFIG} >> ${CONFIG}.bak ; }
 #
#
# Write config
#
echo "CONFIG_ATHOME=${CONFIG_ATHOME}" > ${CONFIG}
echo "CONFIG_SHELL_JAR_FILE=${CONFIG_SHELL_JAR_FILE}" >> ${CONFIG}
