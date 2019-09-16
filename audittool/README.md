# Running Audit tool
## References
[Audit Tool installation and configuration](https://github.com/buda-base/asset-manager/blob/v0.9-alpha/audittool/audit-test-shell/Install.md)

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
Test output is found in `~/audit-test-logs` or `$HOME/audit-test-logs` which is a subfolder of the running user's home directory. This value is configurable.

Test output is saved in two formats:
1. csv file, with a simplified structure. These are designed to be easily pasted into spreadsheet programs.
2. log files, for advanced processing.

Each type produces summary and detail  files.

## Summary output

Summary files simply report on each line:
- the work which was tested
- the test Operation
- the test result.

## Detailed output

Detail output contains more lines, which name each file which caused a test to fail. Summary output is included.

# Configuration
## Property file

`shell.properties`

Audit tool reads several variables from its property file `shell.properties` which is found in the same subdirectory as the shell jar file. These properties locate the tests and define parameters which the tests need, such as the
folder names of parents of image groups.

`log4j2.properties`

Values relating to logging and output appear here. You can configure the parent folder of log files, their formats and file names.
