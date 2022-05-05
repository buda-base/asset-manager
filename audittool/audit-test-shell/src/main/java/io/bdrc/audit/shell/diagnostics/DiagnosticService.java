package io.bdrc.audit.shell.diagnostics;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Properties;

/**
 * Serves up diagnostic properties and values for audit tool
 * Diagnostics        | params,properties, jvm
 * DiagnosticFileName | valid file path
 * AppendDiagnostic   | true,false
 */
public class DiagnosticService {

    //region keywords

    // These are static, so the parser can find them
    public static  Map<String, String[]> getDiagServiceSyntax() {
        return _diagSyntax;
    }


    /**
     * The diagnostic syntax is - the keys of this map are the relevant keywords, the arrays
     * are their possible values. Empty string means any value is allowed.
     * Callers can, but need not, use case-insensitive parsing
     */
    private final static Map<String, String[]> _diagSyntax = ImmutableMap.<String, String[]>builder()
            .put("Diagnostics", new String[]{"params", "properties", "jvm"})
            .put("DiagnosticFileName", new String[]{"<any file path>"})
            .put("AppendDiagnostic", new String[]{"true,false"} )
            .build();

    public Logger getLogger() {
        return _logger;
    }

    public void setLogger(final Logger logger) {
        _logger = logger;
    }

    private Logger _logger ;

    private final DiagnosticEntity _diag ;

    /**
     *
     * @param logger Stream the diagnostics to this logger
     * @param properties command line input of relevant properties, syntax checked
     */
    public DiagnosticService(Logger logger, Map<String, String[]> properties) {
        this.setLogger(logger);
        Properties _diagProperties;
        _diag = new DiagnosticEntity(properties);

    }
}
