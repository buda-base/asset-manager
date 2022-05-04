package io.bdrc.audit.shell.diagnostics;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Serves up diagnostic properties and values for audit tool
 */
public class DiagnosticService {

    private static List<String> _reservedProperties = List.of("")
    public Logger getLogger() {
        return _logger;
    }

    public void setLogger(final Logger logger) {
        _logger = logger;
    }

    private Logger _logger ;

    private DiagnosticEntity _diag ;

    public DiagnosticService(Logger logger, Properties properties, String separator) {
        this.setLogger(logger);
        _diag = new DiagnosticEntity(properties,separator);

    }
}
