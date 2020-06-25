#  Audit Tool Installation

- Current VER is 0.9
- Current REL is SNAPSHOT-1

## Supported Platforms

- Windows 10 with Powershell 3.0 and above
- Mac OS X 10.14 (High Sierra) with Bash 4 (gnu coreutils)
- Debian 10

** Note ** A shell script ending in `.sh` has the same name on windows, but the suffix `.ps1` 

## Download
Download:
- `audit-test-shell-VER-REL-Install.zip`

## Install

### Install procedures
Identify a target directory and unzip `audit-test-shell-VER-REL-Install.zip` into it. This directory will be known as AT_INSTALL_HOME
Please refer to "Updating Guidelines"

## Configuration
### Sample Run

Configuration is performed by running `$AT_INSTALL_HOME/audit-tool-config.sh`


A sample run is shown below:

 ```bash
nobody@Nowhere:audit-test-shell-0.8-SNAPSHOT-2$ ./audit-tool-config.sh
This script prompts you for two files and saves the answers in '/Users/nobody/.config/bdrc/auditTool/config\'.

It asks for the jar file which runs the whole process.
You press [Enter] to accept the defaults.
Press [Enter] when you are ready to continue.
Enter the path to the jar file which launches the process [ default "/Users/djt/bin/am/at/audit-test-shell-0.8-SNAPSHOT-2/audit-test-shell-0.8-SNAPSHOT-2.jar" ]?
nobody@Nowhere:audit-test-shell-0.8-SNAPSHOT-2$
```

### Walkthrough
- audit-tool-config.sh sets  environment variables which the running script, `audittool.sh` refers to:
  - CONFIG_SHELL_JAR_FILE: the file name of the .JAR which launches the tests and logs their results.
  - CONFIG_ATHOME: The folder in which Audit Tool is installed. This is useful, because it allows you to move `audittool.sh` into your path, and it will still run the jar file in the configured path.

Since audit-tool-config.sh stores preferences in a **per-user** folder, you might want to create a template which saves these variables.
You do this by editing `$AT_INSTALL_HOME/DEFAULT-BDRC-AT-CONFIG.sh` When you know the location of the  downloaded files.

#### Installation troubleshooting

The configuration will fail:
- if you have no existing per-user config file,
- and no default config file,
- and you do not type values in at the prompts.

It also warns if the files you enter don't exist, but writes the values anyway, expecting you'll get around to it later.

## Guidelines for Updating an existing installation
It is advisable to unzip an update into a scratch directory and merge any site customizations into it before making it available on a server. 
