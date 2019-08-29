# Audit Tool Operation
## Installation and Configuration
Please refer to [Installation](Install.md) for details of installation.
## Operation
### Starting
Start the audit tool with the `audittool.sh` script. The configuration step above should have initialized  locations of the software which `audittool.sh` needs.
The arguments to audit tool are simply:
```bash
audittool.sh -p workFolder[;workFolder;workFolder...]
```
### Output

#### Location
Audit Tool log outputs are in a subdirectory `audit-tool-logs` of the user's home directory. You can change this in the Audit tool's `log4j2.properties` folder.

#### Contents
|Level|File name|Details|
|----|----|----|
|Summary|`AuditTestShell-SUMMARY-date&Time.log`|Audit Tool writes a summary of each test to the console, and to a summary log file. The summary log file shows the pass/fail status of each test on each given directory.|
|Detail|`AuditTestShell-DETAIL-date&Time.log`|A detailed log file,  shows each file which failed a specific test.|
|Internal|`AuditTestShell-TestInt-date&time.log`| the shell passes in this log4j logger for test internal logging.|

## Principles of operation
Audit tool's initial release runs every test in the test library tests against a complete work. There is no provision yet for running a test against a single image group or random directory.
### Property file
Audit tool reads several variables from its property file `shell.properties.` These properties locate the tests and define parameters which the tests need, such as the
folder names of parents of image groups.
###
The test requirements and functions are outside of the scope of this document. A draft requirements document of the tests 
can be found at [Audit Tool Test Requirements](https://buda-base.github.io/asset-manager/req/tests/)
### Locating the tests
The goal is eventually to have programmers provide separate test libraries. Audit Tool contains a mechanism to get a set of tests
from a library, marshall the test arguments, and launch the test.
### Test output
The tests themselves do not output results. The test framework allows the shell to iterate over the results and act on them.
Initially, these are sent to log files, but we could send them to a database without changing any code, by reconfiguring the logging
to send to a database.

Each run of audit tool produces summary and detail log files, named `AuditTestShell-{SUMMARY|DETAIL}-yyyy-mm-dd-hh-mm-sss.log`
Each test also may create a log file, but that is primarily for debugging.

You can configure log file naming in the `log4j2.properties` file. **NOTE: log4j2 is significantly different from the original log4j.**

## Test Developer's Guide
This section describes how to implement and package different test libraries. The general Audit Tool User doesn't need
this material.
### Locating the tests
#### Test Dictionary
The shell assumes that the package `io.bdrc.am.audit.iaudit` is either in the jar file or on the class path.
This package contains classes that any shell needs to resolve the test classes.
Test location is documented in the property file `shell.properties:`
```bash
# class name of test dictionary. The jar file referenced by the system property (generally
# # specified by the -DtestJar=<path to Jar containing audit tests> command line argument.
# This class must expose a public method named 'getTestDictionary' which returns
# a Java Hashset<String,io.bdrc.am.audit.iaudit.AuditTestConfig> structure, containing a friendly name for the test, and a class which implements
# the io.bdrc.audit.iaudit.IAudit interface.
testDictionaryClassName=io.bdrc.am.audit.audittests.TestDictionary
```

T`testDictionaryClassName` can be in any package. This definition is the library we shipped as 0.8-SNAPSHOT-2

#### Test Config objects

The test dictionary has a dependency on io.bdrc.am.audit.iaudit.AuditTestConfig class. Test developers include this library
in their Jar, and provide Test configuration objects. The test configuration objects provide information to the shell as 
to a test's name, friendly description, class which implements the test (which, again, can be in any package in the library)
. 

![AuditTestConfig](.AuditToolOperation_images/AuditTestConfig.png)

#### `AuditTestConfig` constructor
```
 /**
     * Instantiates a new Audit test config.
     *
     * @param fullName  the full name
     * @param argNames  the arg names
     * @param shortName the short name
     * @param clazz     the clazz
     */
    public AuditTestConfig(String fullName, List<String> argNames, String shortName, Class<?> clazz)
```
To package a test you implement one of these and add it to your TestDictionary.

`audit-test-lib.TestDictionary()` constructor shows `AuditTestConfig` usaqe:

```
 private final Hashtable<String, AuditTestConfig> _TestDictionary = new Hashtable<String, AuditTestConfig>() {
        {
            put("FileSequence", new AuditTestConfig("File Sequence Test",

                    // This statement asserts that the caller has to provide values for these
                    // arguments
                    Arrays.asList(
                            "ArchiveImageGroupParent", "DerivedImageGroupParent"),
                    "FileSequence", FileSequence.class));

            //noinspection ArraysAsListWithZeroOrOneArgument
            put("NoFilesInFolder", new AuditTestConfig("No Files in Root Folder",
                    Arrays.asList(""),
                    "NoFilesInFolder",
                    NoFilesInRoot.class));

            put("NoFoldersInImageGroups", new AuditTestConfig("No folders allowed in Image Group folders",
                    Arrays.asList("ArchiveImageGroupParent", "DerivedImageGroupParent"),"NoFoldersInImageGroups",
                    NoFoldersInImageGroups.class));
        }
    };
```
##### Parameters
The constructor takes these parameters

name|type|description
----|----|----
fullName|`String`|Free form text
argNames|`List<String>`|List of argument names. The caller of the test provides a List of Strings which are K=V pairs. This is a poor man's implementation of Python's `**kwargs`
shortName|`String`|Short mnemonic, for use in scripting. Should not contain spaces. Usually, this is the TestDictionary. key for which this object is the value 
clazz|`Class<?>`|Any class object which implements the `io.bdrc.am.audit.iaudit.IAuditTest` interface.

#### Running a test
A full production instance is available in `audit-test-shell/src/main/java/io/bdrc/am/audit/shell/shell.java`

Once you've acquired its `AuditTestConfig` object, the components of running a test are:
- Instantiating its with its constructor
- setting its path and keyword arguments (`IAudit.setParams()`)
- calling it's `LaunchTest` implementation.

This code fragment of `audit-test-shell` shows this operation
```
          Constructor<IAuditTest> ctor = testClass.getConstructor(Logger.class);
            IAuditTest inst = ctor.newInstance(testLogger);

            inst.setParams((Object[]) params);
            inst.LaunchTest();

            tr = inst.getTestResult();
```
(you can implement your test classes without a Constructor requiring a logger. In this case, the test writer and the shell writer conspired together to require a logger in the constructor.)

#### Examining test results.
`audit-test-interface/io/bdrc/am/audit/iaudit/TestResult` and `TestMessage` define the objects which implement test results.

The `TestResult.Passed()` method contains the overall outcome of the test.
Test messages are retrieved by the `TestResults.getErrors()` method. It's a good idea to have the first error in the list name the container which failed the test, followed by all the specific failure instances for each file. The caller determines the logging disposition.
