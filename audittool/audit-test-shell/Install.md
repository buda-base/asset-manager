#  Audit Tool Installation

- Current VER is 0.9
- Current REL is SNAPSHOT-1

## Supported Platforms

- Windows 10 with Powershell 3.0 and above
- Mac OS X 10.14 (High Sierra) with Bash 4 (gnu coreutils)
- Possibly Linuxes, with Bash 4 (untested)

** Note ** A shell script ending in `.sh` has the same name on windows, but the suffix `.ps1` 

## Download
Download:
- `audit-test-shell-VER-REL-Install.zip`
- `audit-test-lib-VER-REL-jar-with-dependencies.jar`

## Install
### Overview
Audit tool is configured so that the test library is independent of the shell. This allows you to update the test
library by itself. Configuration sets up separate environment variables for the test library and the main running script.

### Install procedures
1. Identify a target directory and unzip `audit-test-shell-VER-REL-Install.zip` into it. This directory will be known as AT_INSTALL_HOME
2. You can move the audit-test-lib jar into any folder. Just make a note of its location for configuration. You may wish to create a link to it. `ln -s <path_to_specific_version> audit-test-lib`

## Configuration
### Sample Run

Configuration is performed by running `$AT_INSTALL_HOME/audit-tool-config.sh`


A sample run is shown below:

 ```bash
nobody@Nowhere:audit-test-shell-0.8-SNAPSHOT-2$ ./audit-tool-config.sh
This script prompts you for two files and saves the answers in '/Users/nobody/.config/bdrc/auditTool/config\'.

First, it asks for the location of the test library. This allows you to update
the tests without re-installing everything.

Second, it asks for the jar file which runs the whole process.
You press [Enter] to accept the defaults.
Press [Enter] when you are ready to continue.
Enter the path to the jar file which contains the tests [ default "/Users/djt/bin/am/at/audit-test-lib-0.8-SNAPSHOT-2-jar-with-dependencies.jar" ]?
Enter the path to the jar file which launches the process [ default "/Users/djt/bin/am/at/audit-test-shell-0.8-SNAPSHOT-2/audit-test-shell-0.8-SNAPSHOT-2.jar" ]?
nobody@Nowhere:audit-test-shell-0.8-SNAPSHOT-2$
```

### Walkthrough
- audit-tool-config.sh sets two environment variables which the running script, `audittool.sh` refers to:
  - CONFIG_TEST_LIB_JAR_FILE: The file name of the .JAR which contains tests
  - CONFIG_SHELL_JAR_FILE: the file name of the .JAR which launches the tests and logs their results.

Since audit-tool-config.sh stores preferences in a **per-user** folder, you might want to create a template which saves these variables.
You do this by editing `$AT_INSTALL_HOME/DEFAULT-BDRC-AT-CONFIG.sh` When you know the location of the  downloaded files.

#### Editing DEFAULT-BDRC-AT-CONFIG.sh
You can
manually replace its `CONFIG_TEST_LIB_JAR_FILE` value with your actual test library jar path.

#### Installation troubleshooting

The configuration will fail:
- if you have no existing per-user config file,
- and no default config file,
- and you do not type values in at the prompts.

It also warns if the files you enter don't exist, but writes the values anyway, expecting you'll get around to it later.
