package io.bdrc.am.audit.shell;

import org.apache.commons.csv.CSVFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.CsvParameterLayout;
import org.apache.logging.log4j.core.lookup.StrLookup;

import java.io.File;
import java.nio.charset.Charset;
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

    //region CSVFormat

    /**
     * @return CSV header for appender
     */
    private String getCsvHeader() {
        return _csvHeader;
    }

    public void setCsvHeader(final String csvFormat) {
        _csvHeader = csvFormat;

        _layout = BuildDefaultLayout(lc);
    }

    private String _csvHeader;
    //endregion

    //region Layout

    private Layout getLayout() {
        return _layout;
    }

    /**
     * layout field
     */
    private Layout _layout;
    //endregion

    //region appenderDirectory

    void setAppenderDirectory(final String appenderDirectory) {
        _appenderDirectory = appenderDirectory;
    }

    private String _appenderDirectory;

    //endregion AppenderDirectory

    public Logger getTestResultLogger() {
        return _testResultLogger;
    }

    public void setTestResultLogger(final String testResultLoggerName) {
        _testResultLogger = lc.getLogger(testResultLoggerName);
    }

    private Logger _testResultLogger;
    // endregion Logger property

    private final LoggerContext lc;
    //endregion

    public AuditTestLogController() {

        this.lc = (LoggerContext) LogManager.getContext(false);
        // Set the default to where the logger puts its files. See log4j2.properties
        String userHome = System.getProperty("user.home");
        String defaultLogLocation = GetLog4jPropertyFromContext(lc, "logRoot");
        setAppenderDirectory(Paths.get(userHome, defaultLogLocation).toString());
    }

    boolean ChangeAppender(final String appenderFileName) {

        Map<String, Appender> existingAppenders = _testResultLogger.getAppenders();
        Appender curAppender = null;
        if (existingAppenders.containsKey(EACHWORKAPPENDER))
        {
            curAppender = existingAppenders.get(EACHWORKAPPENDER);
            // what if I dont stop this
            // will the log go to all the other appenders?
            // yes
            curAppender.stop();

            // See if removing it from root logger needed in addition to testResultLogger
            lc.getConfiguration().getRootLogger().removeAppender(EACHWORKAPPENDER);
            _testResultLogger.removeAppender(curAppender);
        }

        File af = Paths.get(_appenderDirectory, appenderFileName).toFile();

        FileAppender fa = FileAppender.newBuilder()
                                  .withAppend(false)
                                  .withFileName(af.getAbsolutePath())
                                  .setConfiguration(lc.getConfiguration())
                                  .setName(EACHWORKAPPENDER)
                                  .setLayout(getLayout())

                                  .build();

// I think it's just .setConfiguration(lc.getConfiguration()) that casts  upward to abstract.
        // This one aboce compiles
//        FileAppender fa = FileAppender.newBuilder().withAppend(false).withFileName(af.getAbsolutePath())
//                                  .setLayout(getLayout())
//                                  .setName(EACHWORKAPPENDER)
//                                  .setConfiguration(lc.getConfiguration()).build();



        // Apparently you need to add it to the config before adding it to the root logger
        lc.getConfiguration().addLoggerAppender(_testResultLogger, fa);
        // This might be safe, in case the structure changes
//        _testResultLogger.addAppender(lc.getConfiguration().getAppender(fa.getName()));
//        lc.updateLoggers();

        fa.start();

        return true;
    }


    /**
     * @param lc          Logger context
     * @param propertyKey property to fetch
     * @return the logger name defined in the context's configuration property 'testLoggerName'
     */
    String GetLog4jPropertyFromContext(LoggerContext lc, String propertyKey) {

        // Keep the peace. Somehow this can't resolve when you do one line
        StrLookup _lookup = lc.getConfiguration().getStrSubstitutor().getVariableResolver();
        return _lookup.lookup(propertyKey);
    }

    /**
     * Create a CSVParameterlayout, using the class' CSVFormat value as the header list
     *
     * @return
     */
    private Layout BuildDefaultLayout(LoggerContext loggerContext)
    {
//        String appenderTemplateName = GetLog4jPropertyFromContext(lc, "AppenderTemplateName");
//        Map<String, Appender> rootAppenders = loggerContext.getRootLogger().getAppenders();
        // Since the calling code has to change for CSV format, there's no advantage to
        // providing a template in the config
        
        // public CsvParameterLayout(Configuration config, Charset charset, CSVFormat csvFormat, String header, String
        //    footer) {
        // super(config, charset, csvFormat, header, footer);
        Layout layoutTemplate = new CsvParameterLayout(loggerContext.getConfiguration(), Charset.defaultCharset(),
                CSVFormat.DEFAULT.withDelimiter(',').withQuote('"'),
                getCsvHeader(), "");
//        Layout layoutTemplate = rootAppenders.containsKey(appenderTemplateName)
//                        ? rootAppenders.get(appenderTemplateName).getLayout()
//                        : new CsvParameterLayout(loggerContext.getConfiguration(), Charset.defaultCharset(),
//                                CSVFormat.DEFAULT.withDelimiter(',').withQuote('"'),
//                getCsvHeader(), null);

        return layoutTemplate;
    }


}