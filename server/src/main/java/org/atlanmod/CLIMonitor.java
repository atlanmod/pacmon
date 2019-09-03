package org.atlanmod;

import org.apache.commons.cli.*;

public class CLIMonitor {

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        options.addOption(Option.builder("p")
                .longOpt("process")
                .desc("starts a process monitoring")
                .build());

        options.addOption(Option.builder("t")
                .longOpt("thread")
                .desc("starts a thread monitoring")
                .numberOfArgs(2)
                .argName("pid")
                .argName("tid")
                .build());

        options.addOption(Option.builder("f")
                .longOpt("file")
                .desc("file in which the power will be written")
                .numberOfArgs(1)
                .argName("file")
                .build());

        options.addOption(Option.builder("r")
                .longOpt("repo")
                .desc("repo in which a file containing the power will be written")
                .numberOfArgs(1)
                .argName("repo")
                .build());

        options.addOption(Option.builder("pid")
                .desc("pid of the process to monitor")
                .required()
                .build());

        try {
            CommandLine line = parser.parse( options, args );
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "pacmon [OPTION] ... -p [PID]", options );
    }
}
