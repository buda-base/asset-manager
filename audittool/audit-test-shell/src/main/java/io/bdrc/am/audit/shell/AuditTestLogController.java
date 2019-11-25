package io.bdrc.am.audit.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.lookup.StrLookup;

import java.io.File;
import java.nio.file.Paths;
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
    @Deprecated
    public void setLogger(final org.slf4j.Logger testResultLogger) {
        // Because you can't just cast
        // this._testResultLogger = (org.apache.logging.log4j.core.Logger) testResultLogger;
        this._testResultLogger = lc.getLogger(testResultLogger.getName());

    }

    //region appenderDirectory

    public String getAppenderDirectory() {
        return _appenderDirectory;
    }

    public void setAppenderDirectory(final String appenderDirectory) {
        _appenderDirectory = appenderDirectory;
    }

    private String _appenderDirectory;

    //endregion AppenderDirectory

    private Logger _testResultLogger;
    // endregion Logger property

    final LoggerContext lc ;
    //endregion

    public AuditTestLogController() {

        this.lc = (LoggerContext) LogManager.getContext(false);
        // Set the default to where the logger puts its files. See log4j2.properties
        String userHome = System.getProperty("user.home");
        String defaultLogLocation = GetLog4jPropertyFromContext(lc,"logRoot");
        setAppenderDirectory( Paths.get(userHome,defaultLogLocation).toString());
    }

    boolean ChangeAppender(final String appenderFileName) {

        Map<String, Appender> existingAppenders = _testResultLogger.getAppenders();
        Appender curAppender = null;
        if (existingAppenders.containsKey(EACHWORKAPPENDER))
        {
            curAppender = existingAppenders.get(EACHWORKAPPENDER);
            _testResultLogger.removeAppender(curAppender);

        }

        // just leave logging unchanged
        if (curAppender == null)
        {
            return false;
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

        return true;
    }


    /**
     *
     * @param lc Logger context
     * @param propertyKey property to fetch
     * @return the logger name defined in the context's configuration property 'testLoggerName'
     */
    String GetLog4jPropertyFromContext(LoggerContext lc, String propertyKey) {

        // Keep the peace. Somehow this can't resolve when you do one line
        StrLookup _lookup = lc.getConfiguration().getStrSubstitutor().getVariableResolver();
        return _lookup.lookup(propertyKey);
    }

}
