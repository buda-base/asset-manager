package io.bdrc.am.audit.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.util.Map;

/**
 * Creates a new appender for a specific work
 */
class AuditTestLogController {

    private static final String EACHWORKAPPENDER = "PerWorkAppender";
    /*
     *  Operations:
     *  - get and save the root logger
     *  - Create the file appender
     *  - if there's an existing appender, remove it.
     *  - Add the appender
     */
    // https://github.com/openmrs/openmrs-core/blob/master/api/src/main/java/org/openmrs/util/OpenmrsUtil.java#L505


    //region Properties

    /**
     * @return appender layout string
     */
    //region Layout property
    String getLayout() {
        return _Layout;
    }

    /**
     * set appender layout string
     * @param layout new layout format string
     */
    void setLayout(final String layout) {
        _Layout = layout;
    }
    // Just the level and message, until we go to csv
    private String _Layout = " %-5p %m";

    //endregion

    //region Logger property

    /**
     * Sets the logger which gets appender rotated in and out.
     * @param testResultLogger
     */
    public void setLogger(final org.slf4j.Logger testResultLogger) {
        // Because you can't just cast
        // this._testResultLogger = (org.apache.logging.log4j.core.Logger) testResultLogger;
        this._testResultLogger = lc.getLogger(testResultLogger.getName());

    }

    private Logger _testResultLogger;
    // endregion Logger property

    final LoggerContext lc ;
    //endregion

    public AuditTestLogController() {
        this.lc = (LoggerContext) LogManager.getContext(false);
    }

    void ChangeAppender(final String appenderFileName) {

        Map<String, Appender> existingAppenders = _testResultLogger.getAppenders();
        if (existingAppenders.containsKey(EACHWORKAPPENDER))
        {
            Appender curAppender = existingAppenders.get(EACHWORKAPPENDER);
            _testResultLogger.removeAppender(curAppender);

        }

        File af = new File(appenderFileName);

        FileAppender fa = FileAppender.newBuilder().withAppend(false).withFileName(af.getAbsolutePath())
                                  .setLayout(PatternLayout.newBuilder().withPattern(this.getLayout()).build())
                                  .setName(EACHWORKAPPENDER)
                                  .setConfiguration(lc.getConfiguration()).build();
        fa.start();

            // Apparently you need to add it to the config before adding it to the root logger
        lc.getConfiguration().addAppender(fa);

        // This might be safe, in case the structure changes
        _testResultLogger.addAppender(lc.getConfiguration().getAppender(fa.getName()));
        lc.updateLoggers();
    }
}
