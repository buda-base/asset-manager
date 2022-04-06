# Audit Tool User Guide
See 'Installation and Configuration' for system requirements.
This document uses examples from Linux, but identical functions can be performed on any installed platform, except where noted.
## References
[Audit Tool installation and configuration](https://github.com/buda-base/asset-manager/blob/master/audittool/audit-test-shell/AuditToolOperation.md)

## Synopsis

```bash
usage: AuditTest [options] { - | Directory,Directory,Directory}
where:

                 - read folders from standard input

                 Directory,.... is a list of directories separated by ,
[options] are:
 -d,--debug             Show debugging information
 -i,--inputFile <arg>   Input file, one path per line
 ```

 # Audittool input
 Aside from options, the only input is a list of existing directories, each which contains a "work."

 A work is a loosely defined set of folders. The requirements for the contents of a folder are detailed in [an internal Google Document](https://docs.google.com/document/d/1TrjUdoLJd5N90d1vWloRqNrlC144-DPfLrClOLsbhVg/edit) and in a [publicly available document](https://buda-base.github.io/asset-manager/req/tests/)
 ## standard input
 `_tbd_@Tseng:~$ audittool.sh -` will read a pathname from standard input. You can pipe input to audit tool this way.

 ## Input inputFile
 Given a file `shortWorks` which contains a list of paths to works,
 ```
/Users/_tbd_/mnt/Archive/W0FFY001
/Users/_tbd_/mnt/Archive/W0FFY002
/Users/_tbd_/mnt/Archive/W0FFY003
```

running `_tbd_@Tseng:iaAudit$ audittool.sh -i shortWorks`
will execute the tests on each folder in turn.

## Pathname as argument
to just pass paths to audittool on the command line, just invoke with the paths separated by commas:

```bash
_tbd_@Tseng:iaAudit$ audittool.sh /Users/_tbd_/mnt/Archive/W0FFY001,/Users/_tbd_/mnt/Archive/W0FFY002,/Users/_tbd_/mnt/Archive/W0FFY003
```

# Audit tool tests
Tests are found in a named library which the `audittool.sh` script passes to the `auditool` main jar file.
The initial set of tests is specified in [Image capture Test Requirements](https://docs.google.com/document/d/1TrjUdoLJd5N90d1vWloRqNrlC144-DPfLrClOLsbhVg/edit?usp=sharing)

# Test output

## Location
Audit Tool log outputs are in subdirectories of `audit-tool-logs` of the user's home directory. You can change the base folder in the Audit tool's `log4j2.properties` folder.

You can configure log file naming in the `log4j2.properties` file. **NOTE: log4j2 is significantly different from the original log4j.**
Under `audit-tool-logs` are folders containing **csv** and **log**

## Log Contents

### Log
|Level|File name|Details|
|----|----|----|
|Summary|`AuditTestShell-SUMMARY-date&Time.log`|Audit Tool writes a summary of each test to the console, and to a summary log file. The summary log file shows the pass/fail status of each test on each given directory.|
|Detail|`AuditTestShell-DETAIL-date&Time.log`|A detailed log file,  shows each file which failed a specific test.|
|Internal|`AuditTestShell-TestInt-date&time.log`| the shell passes in this log4j logger for test internal logging.|


The detail files contain the summary result. If there were failures, each item which failed is separately listed (see below)
### Comma Separated values
CSV files are output for easier analysis and collection. They follow the log files' naming conventions.

Summary and detail log files have different data formats.
The summary file contains:

|path|test_name|outcome|
|---|---|---|
|\\\TBRCRS3\Archive\W1KG10190|No Files in Root Folder|Passed|

The detail file contains:

|path|error_number|error_test|
|---|---|---|
|\\\TBRCRS3\Archive\W1KG10190|104|Image group folder \\\TBRCRS3\Archive\W1KG10190\archive\W1KG10190-I1KG10192  fails files only test.|
|\\\TBRCRS3\Archive\W1KG10190|103|Image group folder \\\TBRCRS3\Archive\W1KG10190\archive\W1KG10190-I1KG10192  contains directory S0001491.JOB-A|

# Configuration
## Property file

`shell.properties`

Audit tool reads several variables from its property file `shell.properties` which is found in the same subdirectory as the shell jar file. These properties locate the tests and define parameters which the tests need, such as the
folder names of parents of image groups.

`log4j2.properties`

Values relating to logging and output appear here. You can configure the parent folder of log files, their formats and file names.

## Overriding properties

+ As a user, you can override `shell.properties` properties by creating a file `$HOME/.config/bdrc/auditTool/user.properties`
+ As a system administrator, you can override any property (even user properties) by defining them in the VM options
section of the `audittool.sh` (`audittool.ps1` on Windows command line).

In this example, we're overriding the MaximumImageFileSize property to a value slightly smaller than the default.
```shell
java -DMaximumImageFileSize=300K  -DatHome=${CONFIG_ATHOME} -Dlog4j.configurationFile=${LOG_PROPS} -jar ${shellJar}  $@
```

**Note** the evaluation is one-pass. You cannot override the
default user.properties file on the command line. This will not cause the
values of 'other_config.properties' to be read in. Command line
properties are always read last.

```shell
java -DUserConfigPath=wont.be.read.properties  -DatHome=${CONFIG_ATHOME} -Dlog4j.configurationFile=${LOG_PROPS} -jar ${shellJar}  $@
```

Detailed examples are given in Appendix I.
## Warnings as errors
The user can override any error they wish (while still remaining mindful that some errors
must be fixed when submitting works to BDRC or its partners). You do this by adding
numeric values to `shell.properties ErrorsAsWarning` property.
Please refer to the installation's `shell.properties` for the appropriate values.

# Appendix I
## Property overriding example

In this example, we test a work overriding the `MaximumImageFileSize` property. This example shows
runs that use:
- the default property
- a much smaller value, defined in `user.properties`
- an override of that smaller value defined in the shell script.

Ex 1. No `user.properties` file, `shell.properties` value of `400K` used. Tests pass
```shell
❯ ls -l ~/.config/bdrc/auditTool/user.properties
gls: cannot access '/Users/XXX/.config/bdrc/auditTool/user.properties': No such file or directory
❯ audittool.sh -l .   ../../Archive/W8LS68226
starting -l . ../../Archive/W8LS68226
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Archive EXIF Test
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		No Files in Root Folder
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Image EXIF Test
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Web Image Attributes
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		No folders allowed in Image Group folders
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		File Sequence Test
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		File Size Test
```

Ex 2. Using User.properties
Here, the default is set to 40K, and the Image file size test fails.
```shell
❯ grep MaximumImage ~/.config/bdrc/auditTool/user.properties
MaximumImageFileSize=40K
❯ audittool.sh -l .   ../../Archive/W8LS68226
starting -l . ../../Archive/W8LS68226
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Archive EXIF Test
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		No Files in Root Folder
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Image EXIF Test
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Web Image Attributes
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		No folders allowed in Image Group folders
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		File Sequence Test
ERROR Failed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		File Size Test
Errors! returned:1: check logs
```

Ex 3: Raising the user properties value

```shell
❯ grep MaximumImage ~/.config/bdrc/auditTool/user.properties
MaximumImageFileSize=300K
❯ audittool.sh -l .   ../../Archive/W8LS68226
starting -l . ../../Archive/W8LS68226
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Archive EXIF Test
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		No Files in Root Folder
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Image EXIF Test
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Web Image Attributes
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		No folders allowed in Image Group folders
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		File Sequence Test
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		File Size Test
```
Ex 4: Overriding user.properties with VM arguments

In this example, we've defined a much smaller argument in a copy of the command file, and the test
fails.
```shell
❯ grep Maximum ./use_vm_args_to_override.sh
java -DMaximumImageFileSize=30K  -DatHome=${CONFIG_ATHOME} -Dlog4j.configurationFile=${LOG_PROPS} -jar ${shellJar}  $@
❯ ./use_vm_args_to_override.sh -l .   ../../Archive/W8LS68226
starting -l . ../../Archive/W8LS68226
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Archive EXIF Test
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		No Files in Root Folder
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Image EXIF Test
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		Web Image Attributes
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		No folders allowed in Image Group folders
INFO  Passed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		File Sequence Test
ERROR Failed	/Users/jimk/dev/tmp/at/test/../../Archive/W8LS68226		File Size Test
Errors! returned:1: check logs
```
