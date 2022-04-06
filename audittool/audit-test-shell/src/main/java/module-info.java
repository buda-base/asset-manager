module io.bdrc.audit.shell {
    exports io.bdrc.audit.shell;
    requires io.bdrc.audit.IAudit;
    requires io.bdrc.audit.tests;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.logging.log4j.core;
    requires commons.cli;
    requires org.slf4j;
    requires commons.csv;
    // To avoid Cannot initialize scripting support because this JRE does not support it. java.lang.NoClassDefFoundError: javax/script/ScriptEngineManager
    // see https://stackoverflow.com/questions/53714010/log4j2-slf4j-and-java-11
    requires java.scripting;
    requires org.apache.logging.log4j;

}