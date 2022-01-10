module audit.test.lib {
    exports io.bdrc.am.audit.audittests;

    requires org.apache.commons.io;
    requires slf4j.api;
    requires audit.test.IAudit;
    requires java.xml;
    requires java.desktop;
    requires metadata.extractor;
    requires org.apache.commons.imaging;
    requires com.google.common;
    requires org.apache.commons.lang3;
    requires org.apache.httpcomponents.httpcore;
    requires com.twelvemonkeys.imageio.jpeg;
    requires com.twelvemonkeys.imageio.tiff;
    requires com.twelvemonkeys.imageio.core;
}