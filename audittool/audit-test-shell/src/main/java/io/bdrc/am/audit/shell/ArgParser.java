package io.bdrc.am.audit.shell;

// https://commons.apache.org/proper/commons-cli/usage.html
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

class ArgParser {

    // Persist the options
    private CommandLine cl ;

    private final Character pathOption = 'p';

    private Boolean isParsed ;

ArgParser (String [] args) {
    Logger logger = LoggerFactory.getLogger(ArgParser.class);

    // Create the parser
    CommandLineParser clp = new DefaultParser();

    Options options = new Options();

    final Character debugOption = 'd';
    options.addOption(debugOption.toString(), "debug", false, "Show debugging information");
    final Character dirsSeparator = ';';
    options.addOption(Option.builder(pathOption.toString())
            .required()
            .longOpt("paths")
            .hasArg()
            .hasArgs()
            .valueSeparator(dirsSeparator)
            .desc("<path>;<path>;.... Semicolon delimited list of directories to test.")
            .build());
    try {
      cl = clp.parse(options, args);
      isParsed = true;

    } catch (ParseException exc) {
        logger.error("Failed to parse {}", exc.getMessage());

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "AuditTest", options );
        isParsed = false;
    }
}

    /**
     * Extracts the dir arguments
     * @return values of the "p -- paths" argument
     */
    ArrayList<String> getDirs() {

        String[] args = {};
        if (isParsed) {
            if (cl.hasOption(pathOption)) {
                    args =  cl.getOptionValues(pathOption);
            }
        }

        return new ArrayList<>(Arrays.asList(args));

}

}

