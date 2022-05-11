package io.bdrc.audit.shell;

// https://commons.apache.org/proper/commons-cli/usage.html

import io.bdrc.audit.shell.diagnostics.DiagnosticService;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class ArgParser {

    /* Persist the options */
    private final Logger logger = LoggerFactory.getLogger("sys");

    private CommandLine cl;

    private Boolean isParsed;
    private List<String> nonOptionArgs;


    private List<String> definedOptions = new ArrayList<>();
    private List<String> requestedTests = new ArrayList<>();

    // These are class members because they are referenced outside the constructor
    private final String infileOptionShort = "i";
    private final String infileOptionStdin = "-";
    private final String versionShort = "v";
    private final String helpShort = "h";
    private final String diagHelpShort = "d";
    private final String queryTestsShort = "Q";
    private final Options options = new Options();

    // when you need the opposite of this, created it
    private static final boolean ARGS_UNIQUE_REQUIRED = true;

    /**
     * ArgParser. Returns values of arguments.
     *
     * @param args command line input after Java environ vars parsed and removed.
     */
    ArgParser(String[] args) {
        final String infileOptionLong = "inputFile";
        final String logHome = "l";
        final String logHomeLong = "log_home";
        final String helpLong = "help";
        final String versionLong = "version";
        final String defineShort = "D";
        final String defineLong = "Define";
        final String testShort = "T";
        final String testLong = "Tests";
        final String queryTestsLong = "QueryTests";
        final String diagHelpLong = "helpDiag";
        final char colonArgValueSeparator = ':';


        // Create the parser
        CommandLineParser clp = new DefaultParser();

        // options.addOption("d", "debug", false, "Show debugging information");

        // Lists of things
        options.addOption(Option.builder(defineShort)
                .longOpt(defineLong)
                .hasArg(true)
                .valueSeparator(colonArgValueSeparator)
                .argName("Define")
                .desc("-D property=value:property2=value2:... or -D property=value -D property2=value2 ...")
                .build());

        // jimk note this does not have an arg value separator.
        options.addOption(Option.builder(testShort)
                .longOpt(testLong)
                .hasArg(true)
                .valueSeparator(colonArgValueSeparator)
                .desc("List of tests to run. Tests must be shown in -Q Option. Format: -T test1:test2:... or -T " +
                        "test1 " +
                        "-T " +
                        "test2 ...")
                .required(false)
                .build());

        // Other options
        options.addOption(Option.builder(infileOptionShort)
                .longOpt(infileOptionLong)
                .hasArg(true)
                .desc("Input file, one Path to Work per line")
                .build());

        options.addOption(Option.builder(logHome)
                .longOpt(logHomeLong)
                .hasArg(true)
                .desc("Test Result log directory. Must be writable. Default is <UserHome>/audit-test-logs/" +
                        ". Created if not exists ")
                .required(false)
                .build());


        // Query and leave options
        options.addOption(Option.builder(versionShort)
                .longOpt(versionLong)
                .hasArg(false)
                .desc("Shows internal development version (resources)")
                .required(false)
                .build());

        options.addOption(Option.builder(helpShort)
                .longOpt(helpLong)
                .hasArg(false)
                .desc("Usage")
                .required(false)
                .build());

        options.addOption(Option.builder(queryTestsShort)
                .longOpt(queryTestsLong)
                .hasArg(false)
                .desc("Query test names in library")
                .valueSeparator(colonArgValueSeparator)
                .required(false)
                .build());

        options.addOption(Option.builder(diagHelpShort)
                .longOpt(diagHelpLong)
                .hasArg(false)
                .required(false)
                .desc("Prints detailed help for diagnostics")
                .build()
        );


        try {
            cl = clp.parse(options, args);
            isParsed = true;
            cl.getArgList().forEach(z -> logger.debug("Found arg :{}:", z));

            // nonOptionArgs = RecurseParse(cl.getArgList());
            nonOptionArgs = cl.getArgList();
        } catch (ParseException exc) {

            // asset-manager-139
            logger.error("Failed to parse {}", exc.getMessage());
            printHelp(options);
            isParsed = false;
            return;
        }

        // sanity check. One of these must be true
        if (!has_DirList()
                && !getReadStdIn()
                && !HasOnlyShowInfo()
                && !HasOnlyShowTestNames()) {

            // special case - if a dir list is required and not present, complain about that
            if (!has_DirList()
                    && !(
                    getReadStdIn()
                            || HasOnlyShowInfo()
                            || HasOnlyShowTestNames()
            )
            ) {
                logger.error("Selected options require one or more PathToWork.");
            }
            printHelp(options);
            isParsed = false;
        }

        // jimk asset-manager-164 add options on command line
        if (cl.hasOption(defineShort)) {
            definedOptions = splitMultipleArguments(cl.getOptionValues(defineShort),
                    colonArgValueSeparator, ARGS_UNIQUE_REQUIRED);
            definedOptions.forEach(x -> logger.debug("Defined option :{}:", x));
        }

        // jimk asset-manager-165 add requested tests
        if (cl.hasOption(testShort)) {
            requestedTests = splitMultipleArguments(cl.getOptionValues(testShort),
                    colonArgValueSeparator, ARGS_UNIQUE_REQUIRED);
            requestedTests.forEach(x -> logger.debug("Requested test :{}:", x));
        }

        // set up log directory
        if (cl.hasOption(logHome)) {
            Path logDirPath = Paths.get(cl.getOptionValue(logHome)).toAbsolutePath();
            String ldpStr = logDirPath.toString();

            // Log home directory must be writable. Create it now, evaluate result
            if (!madeWritableDir(logDirPath)) {
                logger.error("User supplied path {} cannot be created. Using default", ldpStr);
            } else {
                _logDirectory = ldpStr;
            }
        }

        // region jimk asset-manager-169
        if (HasOnlyShowDiagSyntax()) {
            OnlyShowDiagSyntax();
        }

        // endregion


    }


    /**
     * for arguments which allow multiple values, expand the command line list to include them
     *
     * @param definedOptions the command line set of option values, zero or more of which have
     *                       multiple values
     * @param valueSeparator the value separator (should be the same as the option
     * @param requiresUnique true if the resulting list should only contain distinct values. Eliminates duplicates
     *                       in the output
     * @return Expanded list of arguments
     */
    private List<String> splitMultipleArguments(final String[] definedOptions, final char valueSeparator,
                                                boolean requiresUnique)
    {
        List<String> splittedOptions = new LinkedList<>();
        Arrays.stream(definedOptions).forEach(o ->
                Collections.addAll(splittedOptions, o.split(String.valueOf(valueSeparator))));

        return requiresUnique ? splittedOptions.stream().distinct().toList()
                : splittedOptions;
    }


    /**
     * Get list of options defined on the command line.
     *
     * @return everything defined with -D x or -D x:y:...
     */
    public List<String> getDefinedOptions() {
        return definedOptions;
    }

    /**
     * get requested tests
     *
     * @return tests specified with the -T/-TestNames flag.
     */
    public List<String> getRequestedTests() {
        return requestedTests;
    }

    /**
     * Test for input as argument or as a file
     *
     * @return true when the infile option is set or a list of directories is given on the command line.
     */
    Boolean has_DirList() {
        Boolean rc = cl.hasOption(infileOptionShort) || get_IfArgsCommandLine();
        logger.debug("hasOption {} !getReadStdin {} has_DirList net: {} ", cl.hasOption(infileOptionShort),
                !getReadStdIn
                        (),
                rc);

        return rc;
    }

    /**
     * @return If the user has specified reading from standard input
     */
    Boolean getReadStdIn() {
        logger.debug("non option args empty {}", nonOptionArgs.isEmpty());
        return (!nonOptionArgs.isEmpty()) && nonOptionArgs.get(0).equals
                (infileOptionStdin);
    }

    /**
     * @return If the user has specified reading from standard input
     */
    private Boolean get_IfArgsCommandLine() {
        logger.debug("get_IfArgsCommandLine non option args empty {}", nonOptionArgs.isEmpty());
        return !nonOptionArgs.isEmpty() && !nonOptionArgs.get(0).equals
                (infileOptionStdin);
    }

    /**
     * Extracts the dir arguments
     *
     * @return contents of the "-i --input" argument or the comma-delimited list of arguments
     */
    List<String> getDirs() throws IOException {

        List<String> fileArgs;
        List<String> returned = new LinkedList<>();

        // if we have an
        if (!isParsed) {
            return returned;
        }

        // If an infile was specified, read it
        if (cl.hasOption(infileOptionShort)) {
            String argFile = cl.getOptionValue(infileOptionShort);
            try {
                fileArgs =
                        Files.readAllLines(Paths.get(argFile), StandardCharsets.UTF_8);
                fileArgs.replaceAll(String::trim);

                returned = new ArrayList<>(fileArgs);


            } catch (FileNotFoundException fnfe) {
                logger.error("{} {}", argFile, " not found.");
            } catch (IOException e) {
                e.printStackTrace();
                throw e;

            }
        } else {

            if (!getReadStdIn()) {
                returned = nonOptionArgs;
            }

        }
        return returned;
    }

    /**
     * Overload to test without doing anything
     *
     * @return if we're not actually doing anything
     */
    public Boolean HasOnlyShowInfo()
    {
        return cl.hasOption(helpShort) || cl.hasOption(versionShort) || HasOnlyShowDiagSyntax();
    }

    public Boolean HasOnlyShowTestNames() {
        return cl.hasOption(queryTestsShort);
    }

    public Boolean HasOnlyShowDiagSyntax() {
        return cl.hasOption(diagHelpShort);
    }

    /**
     * Parser has some options which return here only
     */
    public Boolean OnlyShowInfo(String version) {
        boolean rc = false;
        if (cl.hasOption(helpShort)) {
            printHelp(options);
            rc = true;
        }

        // Always show this in debug
        logger.debug("Version {}", version);
        if (cl.hasOption(versionShort)) {
            System.out.printf("Version %s\n", version);
            rc = true;
        }
        return rc;
    }


    /**
     * Show diagnostic help
     */
    private void OnlyShowDiagSyntax() {
        Map<String, String[]> syntax = DiagnosticService.getDiagServiceSyntax();
        System.out.println("Diagnostic Options");

        syntax.forEach((key, value) -> {
            StringBuilder sb = new StringBuilder();
            Arrays.stream(value).forEach(y -> sb.append(String.format("%s,", y)));

            // elide trailing separator
            String allValues = sb.substring(0, sb.length() - 1);
            System.out.printf("arg: %-25sallowed value(s):\t%s\n"
                    , key, allValues);
        });
    }

    private void printHelp(final Options options) {
        new HelpFormatter().printHelp("""
                        audit-tool [options] { - | PathToWork PathToWork ..... }
                        where:
                        \t- read Paths To Works from standard input
                        \tPathToWork ... is a list of directories separated by whitespace
                                                
                        [options] are:""",
                options);
    }

    /**
     * Creates a directory, or checks an existing one for write access
     *
     * @param pathToCreate full path to directory (caller must resolve)
     * @return true if directory is writable, false if not or if it cannot create.
     */
    private boolean madeWritableDir(Path pathToCreate) {

        boolean ok = false;

        if (!Files.exists(pathToCreate)) {
            try {
                Files.createDirectories(pathToCreate);
                ok = Files.isWritable(pathToCreate);
            } catch (IOException ignored) {
            }
        } else {
            if (Files.isDirectory(pathToCreate)) {
                ok = Files.isWritable(pathToCreate);
            }
        }
        return ok;
    }

    private String _logDirectory;

    /**
     * @return Command line value of -l argument, empty string if not given
     */
    String getLogDirectory() {
        return StringUtils.isEmpty(_logDirectory) ? StringUtils.EMPTY : _logDirectory;
    }

    /**
     * @return if parsing was correct
     */
    public Boolean getParsed() {
        return isParsed;
    }


}

