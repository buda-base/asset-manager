module io.bdrc.audit.tests {
    exports io.bdrc.audit.audittests;

    requires org.apache.commons.io;
    requires org.slf4j;
    requires io.bdrc.audit.IAudit;
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