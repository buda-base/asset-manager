#!/usr/bin/env bash
#
# wrapper shell for audittool
# Use maven artifacts for now
# Change as needed
rel=SNAPSHOT
ver=1.0


libJar=audit-test-lib-1.0-SNAPSHOT.jar

# --------  HACK ALERT
# --- until I figure out dependency packaging

libJar=audit-test-lib-1.0-SNAPSHOT.jar
#/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/bin/java
#"-javaagent:/Users/jimk/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-0/183.4886.37/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=61600:/Users/jimk/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-0/183.4886.37/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8
#-classpath /Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/charsets.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/deploy.jar:\
#/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:\
#/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/dnsns.jar:\
#/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/jaccess.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/jfxrt.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/localedata.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/nashorn.jar:\
#/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/sunec.jar:\
#/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/zipfs.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/javaws.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jce.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jfr.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jfxswt.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jsse.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/management-agent.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/plugin.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/resources.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/rt.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/ant-javafx.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/dt.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/javafx-mx.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/packager.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/sa-jdi.jar\
#:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/tools.jar\
#:/Users/jimk/dev/AssetManager/asset-manager/audittool/audit-test-shell/target/classes\
#:/Users/jimk/.m2/repository/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar\:/Users/jimk/.m2/repository/org/apache/logging/log4j/log4j-slf4j-impl/2.11.2/log4j-slf4j-impl-2.11.2.jar\
#:/Users/jimk/.m2/repository/org/apache/logging/log4j/log4j-api/2.11.2/log4j-api-2.11.2.jar\
#:/Users/jimk/.m2/repository/org/apache/logging/log4j/log4j-core/2.11.2/log4j-core-2.11.2.jar\
#:/Users/jimk/dev/AssetManager/asset-manager/audittool/audit-test-interface/target/classes:\
#/Users/jimk/dev/AssetManager/asset-manager/audittool/audit-test-lib/target/classes\
#:/Users/jimk/.m2/repository/commons-io/commons-io/2.0.1/commons-io-2.0.1.jar\
#:/Users/jimk/.m2/repository/org/apache/commons/commons-lang3/3.8.1/commons-lang3-3.8.1.jar\
#:/Users/jimk/.m2/repository/commons-cli/commons-cli/1.4/commons-cli-1.4.jar io.bdrc.am.audit.shell.shell
java -classpath \
${libJar}:audit-test-shell-${ver}-${rel}.jar:audit-test-interface-${ver}-${rel}.jar\
:/Users/jimk/.m2/repository/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar\
:/Users/jimk/.m2/repository/org/apache/logging/log4j/log4j-slf4j-impl/2.11.2/log4j-slf4j-impl-2.11.2.jar\
:/Users/jimk/.m2/repository/org/apache/logging/log4j/log4j-api/2.11.2/log4j-api-2.11.2.jar\
:/Users/jimk/.m2/repository/org/apache/logging/log4j/log4j-core/2.11.2/log4j-core-2.11.2.jar\
:/Users/jimk/.m2/repository/commons-io/commons-io/2.0.1/commons-io-2.0.1.jar\
:/Users/jimk/.m2/repository/org/apache/commons/commons-lang3/3.8.1/commons-lang3-3.8.1.jar\
:/Users/jimk/.m2/repository/commons-cli/commons-cli/1.4/commons-cli-1.4.jar \
 io.bdrc.am.audit.shell.shell -p $*
# java -cp ${libJar}:audit-test-interface-${ver}-${rel}.jar:audit-test-interface-${ver}-${rel}.jar io.bdrc.am.main.shell.shell -p $*