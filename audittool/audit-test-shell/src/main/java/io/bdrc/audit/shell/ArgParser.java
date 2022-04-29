package io.bdrc.audit.shell;

// https://commons.apache.org/proper/commons-cli/usage.html

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class ArgParser {

    /* Persist the options */
    private final Logger logger = LoggerFactory.getLogger("sys");

    private CommandLine cl;

    private Boolean isParsed;
    private List<String> nonOptionArgs;


    private List<String> definedOptions = new ArrayList<>();

    // These are class members because they are referenced outside the constructor
    private final String infileOptionShort = "i";
    private final String infileOptionStdin = "-";
    private final String versionShort = "v";
    private final String helpShort = "h";
    private final String queryTestsShort = "Q";
    private final Options options = new Options();

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
        final char colonArgValueSeparator = ':';


        // Create the parser
        CommandLineParser clp = new DefaultParser();

        options.addOption("d", "debug", false, "Show debugging information");

        Option frelm = Option.builder(defineShort)
                .longOpt(defineLong)
                .hasArg(true)
                .numberOfArgs(45)
                .valueSeparator(colonArgValueSeparator)
                .argName("Define")
                .desc("-D property=value:property2=value2:... or -D property=value -D property2=value2 ...")
                .build();
        options.addOption(frelm);

        options.addOption(Option.builder(infileOptionShort)
                .longOpt(infileOptionLong)
                .hasArg(true)
                .desc("Input file, one Path to Work per line")
                .build());

//         instead of adding to the group, add to mainline options
        options.addOption(Option.builder(logHome)
                .longOpt(logHomeLong)
                .hasArg(true)
                .desc("Test Result log directory. Must be writable. Default is <UserHome>/audit-test-logs/" +
                        ". Created if not exists ")
                .required(false)
                .build());


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

        options.addOption(Option.builder(testShort)
                .longOpt(testLong)
                .hasArg(false)
                .desc("List of tests to run. Tests must be shown in -Q Option. Format: -T test1:test2:... or -T " +
                        "test1 " +
                        "-T " +
                        "test2 ...")
                .required(false)
                .build());

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
        if (!has_DirList() && !getReadStdIn() && !OnlyShowInfo() && !OnlyShowTestNames()) {
            printHelp(options);
            isParsed = false;
        }

        // jimk asset-manager-164 add options on command line
        if (cl.hasOption(defineShort)) {
            definedOptions =  Arrays.asList(cl.getOptionValues(defineShort));
            definedOptions.forEach(x -> logger.debug("Defined option {}", x));
        }

        if (cl.hasOption(testShort)) {
            List <String> requestedTests = Arrays.asList(cl.getOptionValues(testShort));
        }
        if (cl.hasOption("l")) {
            Path logDirPath = Paths.get(cl.getOptionValue("l")).toAbsolutePath();
            String ldpStr = logDirPath.toString();

            // Log home directory must be writable. Create it now, evaluate result
            if (!madeWritableDir(logDirPath)) {
                printHelp(options);
                logger.error("User supplied path {} cannot be created. Using default", ldpStr);
            } else {
                _logDirectory = ldpStr;
            }
        }
    }


    /**
     * Get list of options defined on the command line.
     * @return everything defined with -D x or -D x:y:...
     */
    public List<String> getDefinedOptions() {
        return definedOptions;
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
    public Boolean OnlyShowInfo()
    {
        return cl.hasOption(helpShort) || cl.hasOption(versionShort);
    }

    public Boolean OnlyShowTestNames () {
        return cl.hasOption(queryTestsShort);
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

    private void printHelp(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("audit-tool [options] { - | PathToWork PathToWork ..... }\nwhere:\n" +
                        "\t- read Paths To Works from standard input\n" +
                        "\tPathToWork ... is a list of directories separated by whitespace\n" +
                        "[options] are:",
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
     * @return Command line value of -l argument
     */
    String getLogDirectory() {
        return _logDirectory;
    }

    /**
     * @return if parsing was correct
     */
    public Boolean getParsed() {
        return isParsed;
    }


}

