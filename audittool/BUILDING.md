# Build and Release
# Build
All versions of Audit tool can be built using maven. The relevant hierarchy is 

```
├── audittool
│   ├── pom.xml
│   ├── IAudit
│   │   ├── pom.xml
│   ├── audit-test-shell
│   │   ├── pom.xml
│   └── test-lib
│   │   ├── pom.xml
```

Each submodule references its parent by this node:
```xml
    <parent>
        <artifactId>audit-tool</artifactId>
        <groupId>io.bdrc.audit</groupId>
        <version>1.0-beta-1</version>
    </parent>
```

Note that the parent version must be hard coded.

In either the parent or the submodules, you can work with the complete Maven phases. Typically, 
developers would build using the

`mvn clean install` 

command in the auditool directory.

### Building for debugging
In the IntelliJ environment, you configure the debugger to `mvn compile` before launching.
This is shown in the resulting config file: `audittool/.run/shell.run.xml`
(See Intellij Menu --> Run --> Edit Build Configurations --> Application --> Shell) for the UI selections that built
the `shell.run.xml` file.)

The compile phase also copies resources file from `src/scripts` into the output folder for testing and packaging.

## Packaging
The work of pulling together an audit-tool distributable is done in the `audit-test-shell` module's `pom.xml`, in 
the `package` phase. It uses the `maven-assembly-plugin` whose configuration given in
`audit-test-shell/src/main/assembly/shell-assembly.xml`
Essentially creates a zip file and a directory that can be copied and manually installed
anywhere.

This will unpack an upgrade to v0.9, but it will still need v0.9's special shell script and environment
scaffolding (see Install-0.9.md) 

## Building an installation kit
Building an installation kit is as simple as using 

```shell
cd ${audittool local_git_repo_home}
mvn clean install
mvn package # optional, if this is the first time you're building on the target platform
cd ${audittool local_git_repo_home}/audit-test-shell
jpackage @src/main/script/jpackage_${platform}.conf
```

where `${platform}` is one of `{debian|win|MacOS}` and matches the system you are running on.
(`jpackage` only builds executables for the platforms it runs on)
