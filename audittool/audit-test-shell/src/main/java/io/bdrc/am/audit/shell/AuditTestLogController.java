package io.bdrc.am.audit.shell;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.CsvParameterLayout;
import org.apache.logging.log4j.core.lookup.StrLookup;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Creates a new appender for a specific work
 */
class AuditTestLogController {

    private static final String EACHWORKAPPENDER = "PerWorkAppender";
    private static String DEFAULTLOGDIR;

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

    void setCsvHeader(final String csvFormat) {
        _csvHeader = csvFormat;

        _layout = BuildDefaultLayout(_loggerContext);
    }

    private String _csvHeader;
    //endregion

    //region Layout

    private Layout<String> getLayout() {
        return _layout;
    }

    /**
     * layout field
     */
    private Layout<String> _layout;
    //endregion

    //region appenderDirectory

    /**
     * Sets the appender directory
     * @param appenderDirectory relative or absolute path spec
     */
    void setAppenderDirectory( String appenderDirectory) {

        _appenderDirectory = Paths.get(StringUtils.isEmpty(appenderDirectory) ? DEFAULTLOGDIR : appenderDirectory).toFile()
                .getAbsolutePath();
    }

    private String _appenderDirectory;

    //endregion AppenderDirectory

    //region testResultLogger

    void setTestResultLogger(final String testResultLoggerName) {
        _testResultLogger = _loggerContext.getLogger(testResultLoggerName);
    }

    private Logger _testResultLogger;


    private final LoggerContext _loggerContext;
    //endregion

    //region appenderFileAndDir
    private FileAppender _fileAppender;

    FileAppender GetFileAppender()
    {
        return _fileAppender;
    }

    synchronized void SetFileAppender(FileAppender value)
    {
        _fileAppender = value;
    }
    //endregion

    // region Constructor
    public AuditTestLogController() {

        this._loggerContext = (LoggerContext) LogManager.getContext(false);
        // Set the default to where the logger puts its files. See log4j2.properties.
        // Should be fully qualified.
        DEFAULTLOGDIR = GetLog4jPropertyFromContext(_loggerContext, "logRoot");
        setAppenderDirectory(Paths.get(DEFAULTLOGDIR).toAbsolutePath().toString());
    }
    // endregion

    //region public methods
    void ChangeAppender(final String appenderFileName) {

        Map<String, Appender> existingAppenders = _testResultLogger.getAppenders();
        Appender curAppender;
        if (existingAppenders.containsKey(EACHWORKAPPENDER))
        {
            curAppender = existingAppenders.get(EACHWORKAPPENDER);
            // what if I dont stop this
            // will the log go to all the other appenders?
            // yes
            curAppender.stop();

            // See if removing it from root logger needed in addition to testResultLogger
            _loggerContext.getConfiguration().getRootLogger().removeAppender(EACHWORKAPPENDER);
            _testResultLogger.removeAppender(curAppender);
        }

        File af = Paths.get(_appenderDirectory, appenderFileName).toFile();

        SetFileAppender( FileAppender.newBuilder()
                                  .withAppend(false)
                                  .withFileName(af.getAbsolutePath())
                                  .setConfiguration(_loggerContext.getConfiguration())
                                  .setName(EACHWORKAPPENDER)
                                  .setLayout(getLayout())

                                  .build());

// I think it's just .setConfiguration(_loggerContext.getConfiguration()) that casts  upward to abstract.
        // This one above compiles and runs
//        FileAppender fa = FileAppender.newBuilder().withAppend(false).withFileName(af.getAbsolutePath())
//                                  .setLayout(getLayout())
//                                  .setName(EACHWORKAPPENDER)
//                                  .setConfiguration(_loggerContext.getConfiguration()).build();



        _loggerContext.getConfiguration().addLoggerAppender(_testResultLogger, _fileAppender);
        // This might be safe, in case the structure changes
//        _testResultLogger.addAppender(_loggerContext.getConfiguration().getAppender(fa.getName()));
//        _loggerContext.updateLoggers();

        _fileAppender.start();

    }

    /**
     * Rename the log file to indicate test passed
     */
    void RenameLogPass() throws IOException {
        RenameFile("passPrefix");

    }

    void RenameLogFail() throws IOException
    {
        RenameFile("failPrefix");
    }

    // endregion

    // region Private Methods

    /**
     * Rename the file using a the value of a log4j2.property
     * as the prefix
     * @param propertyKey
     */
    private void RenameFile(String propertyKey) throws IOException
    {
        String prefixValue = GetLog4jPropertyFromContext(this._loggerContext,propertyKey);

        // nothing to do
        if (StringUtils.isEmpty(prefixValue))
        {
            return;
        }
        Path appenderPath= Paths.get(GetFileAppender().getFileName());
        String appenderDirName = appenderPath.getParent().toString();

        // Create the dest file path from the folder, the prefix and the file name
        String destFileName = prefixValue + appenderPath.getFileName().toString();
        Path destPath = Paths.get(appenderDirName,destFileName);
        Files.move(appenderPath,destPath);

    }
    /**
     * @param lc          Logger context
     * @param propertyKey property to fetch
     * @return the logger name defined in the context's configuration property 'testLoggerName' or empty string if not
     * found.
     */
    private String GetLog4jPropertyFromContext(LoggerContext lc, String propertyKey) {

        // Keep the peace. Somehow this can't resolve when you do one line
        StrLookup _lookup = lc.getConfiguration().getStrSubstitutor().getVariableResolver();
        String retStr = _lookup.lookup(propertyKey);
        return (StringUtils.isEmpty(retStr) ? StringUtils.EMPTY : retStr);
    }

    /**
     * Create a CSVParameterlayout, using the class' CSVFormat value as the header list
     *
     * @return Log4JLayout object
     */
    private Layout<String> BuildDefaultLayout(LoggerContext loggerContext)
    {
        return new CsvParameterLayout(loggerContext.getConfiguration(), Charset.defaultCharset(),
                CSVFormat.DEFAULT.withDelimiter(',').withQuote('"'),
                getCsvHeader(), "");
    }

    //endregion


}