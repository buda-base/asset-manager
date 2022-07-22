module io.bdrc.audit.IAudit {
    exports io.bdrc.audit.iaudit;
    exports io.bdrc.audit.iaudit.message;

    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
    requires org.slf4j;
}