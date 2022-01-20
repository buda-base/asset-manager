module audit.test.shell {
    exports io.bdrc.am.audit.shell;
    requires audit.test.IAudit;
    requires commons.csv;
    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;
    requires org.apache.logging.log4j.core;
    requires commons.cli;
    requires audit.test.lib;
    requires org.slf4j;

}