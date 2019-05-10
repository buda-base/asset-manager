#  Audit Tool Installation
Installation media is at [Github BUDA-base Audit Tool Install](https://github.com/buda-base/asset-manager/tree/master/audittool/install)

- Current VER is 0.8
- Current REL is SNAPSHOT-2

## Download
To install this snapshot release, see [V0.8 Release](https://github.com/buda-base/asset-manager/releases/tag/v0.8-SNAPSHOT-2)
Go to [Installation media]((https://github.com/buda-base/asset-manager/tree/master/audittool/install)
). Download:
- audit-test-shell-VER-REL-jskFormat.zip
- audit-test-lib-VER-REL-jar-with-dependencies.jar

## Install
### Overview
Audit tool is configured so that the test library is independent of the shell. This allows you to update the test
library by itself. Configuration sets up separate environment variables for the test library and the main running script.
- `CONFIG_TEST_LIB_JAR_FILE` is the path to the test library.
- `CONFIG_SHELL_JAR_FILE` is the path to the startup jar file.
The shell script which launches audit tool, `audittool.sh` uses these environment variables.

### Install procedures
1. Identify a target directory and unzip `audit-test-shell*.zip` into it. This directory will be known as AT_INSTALL_HOME
2. You can move the audit-test-lib jar into the resulting folder, or into some other location. Just make a note of its location for configuration. You may wish to create a link to it. `ln -s <path_to_specific_version> audit-test-lib`

## Configuration 
### Default configuration
Audit tool provides a default configuration  in `AT_INSTALL_HOME/DEFAULT-BDRC-AT-CONFIG.sh` After download, you can 
manually replace its `CONFIG_TEST_LIB_JAR_FILE` with your actual test library jar path.
This allows you to configure Audit tool for all users on a system. 

**The system-wide configuration will be lost when you re-install Audit Tool**

### Per user configuration
Run `$AT_INSTALL_HOME/auditTool-config.sh` to configure Audit tool for your own use.

A sample run is shown below:
 ```bash
nobody@Nowhere:audit-test-shell-0.8-SNAPSHOT-2$ ./auditTool-config.sh
This script prompts you for two files and saves the answers in \'/Users/nobody/.config/bdrc/auditTool/config\'.

First, it asks for the location of the test library. This allows you to update
the tests without re-installing everything.

Second, it asks for the jar file which runs the whole process.
You press [Enter] to accept the defaults.
Press [Enter] when you are ready to continue.
Enter the path to the jar file which contains the tests [ default "/Users/jimk/bin/am/at/audit-test-lib-0.8-SNAPSHOT-2-jar-with-dependencies.jar" ]?
Enter the path to the jar file which launches the process [ default "/Users/jimk/bin/am/at/audit-test-shell-0.8-SNAPSHOT-2/audit-test-shell-0.8-SNAPSHOT-2.jar" ]?
nobody@Nowhere:audit-test-shell-0.8-SNAPSHOT-2$
```

The configuration will fail if you have no config file, and no default config file, and you do not type values in at the prompts.
It also warns if the files you enter don't exist (but writes them anyway, figuring you'll get around to it later)
