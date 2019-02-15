package io.bdrc.am.audit.shell;

// https://commons.apache.org/proper/commons-cli/usage.html
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Arrays;

class ArgParser {
ArgParser (String [] args) {

// Create the parser  TODO
//CommandLineParser clp = new DefaultParser();
//
//Options options = new Options();
//
//options.addOption("d","debug",false,"Show debugging information");
//options.addOption(Option.builder("a").withArgName("root")
//try
    _args = args;
}

private String [] _args ;

   ArrayList<String> getArgs() {
        return new ArrayList<>(Arrays.asList( _args));
    }


}

