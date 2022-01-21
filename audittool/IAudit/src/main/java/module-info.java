module io.bdrc.audit.IAudit {
    exports io.bdrc.am.audit.iaudit;
    exports io.bdrc.am.audit.iaudit.message;

    requires org.apache.commons.lang3;
    requires org.slf4j;
}