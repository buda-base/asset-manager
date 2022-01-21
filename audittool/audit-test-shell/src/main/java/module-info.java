module io.bdrc.audit.shell {
    exports io.bdrc.am.audit.shell;
    requires io.bdrc.audit.IAudit;
    requires io.bdrc.audit.tests;
    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;
    requires org.apache.logging.log4j.core;
    requires commons.cli;
    requires org.slf4j;
    requires commons.csv;

}