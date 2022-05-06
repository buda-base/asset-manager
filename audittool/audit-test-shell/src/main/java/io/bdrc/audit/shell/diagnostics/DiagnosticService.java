package io.bdrc.audit.shell.diagnostics;

import com.google.common.collect.ImmutableMap;
import io.bdrc.audit.iaudit.PropertyManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;

import java.util.*;

/**
 * Serves up diagnostic properties and values for audit tool
 * Diagnostics        | params,properties, jvm
 * DiagnosticFileName | valid file path
 * AppendDiagnostic   | true,false
 */
public class DiagnosticService {

    //region keywords
    private final static String DIAG_DIRECTIVE = "Diagnostics";
    private final static String DIAG_KWD_PARAM = "params";
    private final static String DIAG_KWD_PROPS = "properties";
    private final static String DIAG_KWD_ENV = "env";
    private final static String[] DIAG_KEYWORDS = {DIAG_KWD_PARAM,DIAG_KWD_PROPS,DIAG_KWD_ENV};
    private final static String DIAG_FILE_NAME_DIRECTIVE = "DiagnosticFileName";
    private final static String DIAG_FILE_APPEND_DIRECTIVE = "AppendDiagnostic";


    // These are static, so the parser can find them
    public static Map<String, String[]> getDiagServiceSyntax() {
        return _diagSyntax;
    }

    public final static String BASE_RESOURCE_FILE_KEY = "_SPECIAL_SHELL_PROPS_LOCATION";

    /**
     * The diagnostic syntax is - the keys of this map are the relevant keywords, the arrays
     * are their possible values. The arguments arent parsed. Only recognized values
     * are used - othe values are ignored.
     * Callers can, but need not, use case-insensitive parsing
     */
    private final static Map<String, String[]> _diagSyntax = ImmutableMap.<String, String[]>builder()
            .put(DIAG_DIRECTIVE, DIAG_KEYWORDS)
            .put(DIAG_FILE_NAME_DIRECTIVE, new String[]{"<any file path>"})
            .put(DIAG_FILE_APPEND_DIRECTIVE, new String[]{"true,false"})
            .build();

    public Logger getLogger() {
        return _logger;
    }

    public void setLogger(final Logger logger) {
        _logger = logger;
    }

    private Logger _logger;
    private PropertyManager _propertyRepo;

    private final DiagnosticEntity _diag;

    /**
     * @param logger     Stream the diagnostics to this logger. If directives contains
     *                   the 'DiagnosticFileName' log to that as well.
     * @param pm  PropertyManager (environmental context)
     */
    public DiagnosticService(Logger logger, PropertyManager pm) {
        this.setLogger(logger);
        _propertyRepo = pm;
        Properties diagProps = new Properties() {{
            put(DIAG_DIRECTIVE, _propertyRepo.getPropertyString(DIAG_DIRECTIVE));
            put(DIAG_FILE_NAME_DIRECTIVE, _propertyRepo.getPropertyString(DIAG_FILE_NAME_DIRECTIVE));
            put(DIAG_FILE_APPEND_DIRECTIVE, _propertyRepo.getPropertyString(DIAG_FILE_APPEND_DIRECTIVE));
        }};
        _diag = new DiagnosticEntity(diagProps);
    }

    /**
     * Process the diagnostic directives given by the diagnostic directives
     *
     * @param args            audit tool command line
     * @param propertyManager property controller
     */
    public void ProcessDiagnosticDirectives(final String[] args, PropertyManager propertyManager) {


        // Is the "DiagnosticFileName" directive given? If so, create a file appender for the logger
        // If the "AppendDiagnostic" is set to true, set the appender to append to the file.
        // Logger coreLogger = (Logger)LogManager.getLogger(getLogger());

        // Use the console layout
        // Any directives at all?
        if (0 == _diag.getProperties().size()) {
            return;
        }

        List<String> appendFlags = _diag.getValues(DIAG_FILE_APPEND_DIRECTIVE);
        boolean appendFlag = false;
        if (appendFlags.size() >0) {
            appendFlag = Boolean.parseBoolean(appendFlags.get(0));
        }
        Appender diagAppender = startLogAppender(appendFlag);
        try {
            Boolean showParams = diagsShowValue(_diag,DIAG_KWD_PARAM);
            Boolean showProperties = diagsShowValue(_diag,DIAG_KWD_PROPS);
            Boolean showENV = diagsShowValue(_diag,DIAG_KWD_ENV);

            if (showParams)
            {
                StringBuilder sb = new StringBuilder();
                _logger.info("---------   command line  -------------");
                Arrays.stream(args).forEach( s -> sb.append(String.format("%s ",s)));
                _logger.info(sb.toString());
            }

            if (showProperties) {
                _logger.info("---------   audit-tool properties  -------------");


            }


        } finally {
            stopLogAppender(diagAppender);
        }

    }

    private Boolean diagsShowValue(final DiagnosticEntity diag, final String diagKwdParam) {
        return diag.getValues(DIAG_DIRECTIVE)
                .stream()
                .map(String::toUpperCase)
                .toList()
                .contains(diagKwdParam.toUpperCase(Locale.ROOT));
    }

    /**
     * Create an appender if needed
     * @param appendCurrent true if append to log file
     * @return the file appender, for caller to control
     */
    private Appender startLogAppender(boolean appendCurrent) {

        List<String> diagFileNames = _diag.getValues(DIAG_FILE_NAME_DIRECTIVE);

        if (diagFileNames.isEmpty()) {
            return null;
        }
        String diagFileName = diagFileNames.get(0);

        //https://stackoverflow.com/questions/15441477/how-to-add-log4j2-appenders-at-runtime-programmatically
        org.apache.logging.log4j.core.Logger coreLogger
                = (org.apache.logging.log4j.core.Logger) _logger;

        Appender fileAppender = FileAppender.newBuilder()
                .setName("File")
                .withFileName(diagFileName)
                .withAppend(appendCurrent)
                .setLayout(PatternLayout.newBuilder()
                        .withPattern("%m%n")
                        .build())
                .build();

        fileAppender.start();
        coreLogger.addAppender(fileAppender);
        return fileAppender;
    }

    private void stopLogAppender(Appender appender) {

        if (appender == null) return;

        //https://stackoverflow.com/questions/15441477/how-to-add-log4j2-appenders-at-runtime-programmatically
        org.apache.logging.log4j.core.Logger coreLogger
                = (org.apache.logging.log4j.core.Logger) _logger;
        appender.stop();
        coreLogger.removeAppender(appender);

    }
}
