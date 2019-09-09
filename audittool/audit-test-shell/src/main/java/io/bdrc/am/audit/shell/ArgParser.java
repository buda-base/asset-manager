package io.bdrc.am.audit.shell;

// https://commons.apache.org/proper/commons-cli/usage.html

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ArgParser {

    /* Persist the options */
    private CommandLine cl;

    private final String infileOptionShort = "i";
    private final String infileOptionStdin = "-";
    private final String argsep = ",";

    private final Logger logger = LoggerFactory.getLogger(ArgParser.class);

    private Boolean isParsed;
    private List<String> nonOptionArgs;

    /**
     * ArgParser. Returns values of arguments.
     *
     * @param args command line input after Java enviro vars parsed and removed.
     */
    ArgParser(String[] args) {


        // Create the parser
        CommandLineParser clp = new DefaultParser();

        Options options = new Options();

        options.addOption("d", "debug", false, "Show debugging information");

        final String infileOptionLong = "inputFile";
        final String infileOptionStdinLong = "standard input";

        options.addOption(Option.builder(infileOptionShort)
                .longOpt(infileOptionLong)
                .hasArg()
                .desc("Input file, one path per line")
                .build());

        // instead of adding to the group, add to mainline options
        // inputOptions.addOption(Option.builder(infileOptionStdin)
//        options.addOption(Option.builder(infileOptionStdin)
//                .longOpt(infileOptionStdinLong)
//                .hasArg()
//                .desc("Read Input file from Stdin")
//                .build());


        try {
            cl = clp.parse(options, args);
            isParsed = true;
            nonOptionArgs = cl.getArgList();
        } catch (ParseException exc) {
            logger.error("Failed to parse {}", exc.getMessage());

            printHelp(options);
            isParsed = false;
        }

        // sanity check. One of these must be true
        if (!has_input() && !getReadStdIn())
        {
            printHelp(options);
            isParsed = false;
        }
    }

    /**
     * Test for input as argument or as a file
     *
     * @return true when the infile option or a list of directories is given on the command line.
     */
    Boolean has_input() {
        return cl.hasOption(infileOptionShort) || !getReadStdIn() ;
    }

    /**
     *
     * @return If the user has specified reading from standard input
     */
    Boolean getReadStdIn() {
        return (!nonOptionArgs.isEmpty()) && nonOptionArgs.get(0).equals
                (infileOptionStdin);
    }

    /**
     * Extracts the dir arguments
     *
     * @return contents of the "-i --input" argument or the ,delimited list of arguments
     */
    ArrayList<String> getDirs() throws IOException {

        String[] args;
        List<String> fileArgs;
        ArrayList<String> returned = new ArrayList<>();

        // if we have an
        if (!isParsed)
            return returned;

        // If an infile was specified, read it
        if (cl.hasOption(infileOptionShort)) {
            String argFile = cl.getOptionValue(infileOptionShort);
            try {
                fileArgs =
                        Files.readAllLines(Paths.get(argFile), StandardCharsets.UTF_8);
                returned = new ArrayList<>(fileArgs);

            } catch (FileNotFoundException fnfe) {
                logger.error("{} {}", argFile, " not found.");
            } catch (IOException e) {
                e.printStackTrace();
                throw e;

            }
        }
        else {
            // If args isnt the magic stdin delimiter, parse a delimited list of paths
            String inArgs = nonOptionArgs.get(0);
            if (!getReadStdIn())
            {
                returned = new ArrayList<>(Arrays.asList(inArgs.split(argsep)));
            }

        }
        return returned;
    }


    private void printHelp(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("AuditTest [options] { - | Directory,Directory,Directory}\nwhere:\n\t- read " +
                        "folders from " +
                        "standard input\n\t" +
                        "Directory,.... is a list of directories separated by ,\n[options] are:",
                options);
    }


}

