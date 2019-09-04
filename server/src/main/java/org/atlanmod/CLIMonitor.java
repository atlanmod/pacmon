package org.atlanmod;

import com.google.common.io.Files;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CLIMonitor {

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        Collection<Option> optionList = new ArrayList<>();

        Option process = Option.builder("p")
                .longOpt("process")
                .desc("starts a process monitoring")
                .build();

        Option thread = Option.builder("t")
                .longOpt("thread")
                .desc("starts a thread monitoring")
                .numberOfArgs(1)
                .argName("tid")
                .build();

        Option repo = Option.builder("r")
                .longOpt("repo")
                .desc("repo in which a file containing the power will be written")
                .numberOfArgs(1)
                .argName("repo")
                .build();

        Option pid = Option.builder()
                .longOpt("pid")
                .desc("pid of the process to monitor")
                .numberOfArgs(1)
                .argName("pid")
                .build();

        Option help = Option.builder("h")
                .longOpt("help")
                .desc("display this message")
                .build();

        Arrays.asList(process, thread, pid, repo, help).forEach(options::addOption);

        try {
            CommandLine line = parser.parse( options, args );
            List<Option> chosenOptions = Arrays.asList(line.getOptions());

            if (chosenOptions.contains(help)) {
                printHelp(options);
            } else if (!chosenOptions.contains(pid)) {
                throw new ParseException("you must set a PID");
            }

            int pidToMonitor = Integer.parseInt(line.getOptionValue(pid.getLongOpt()));

            File traceDestination;

            if (chosenOptions.contains(repo)) {
                traceDestination = new File(line.getOptionValue(repo.getLongOpt()));
                if (!traceDestination.exists())
                    traceDestination.mkdirs();
            } else {
                traceDestination = Files.createTempDir();
                if (!traceDestination.exists())
                    traceDestination.mkdirs();
            }

            MappedBusBasedMonitor mappedBusBasedMonitor = new MappedBusBasedMonitor(traceDestination, pidToMonitor);

            if (chosenOptions.contains(process)) {
                //Process level monitoring
                mappedBusBasedMonitor.buildMonitorPowerApi();
            } else if (chosenOptions.contains(thread)) {
                //Thread level monitoring
                int tid = Integer.parseInt(line.getOptionValue(thread.getOpt()));
                mappedBusBasedMonitor.buildMonitorThreadLevel(tid);
            } else {
                throw new ParseException("Must have either --thread or --process");
            }

            mappedBusBasedMonitor.run();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            printHelp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "pacmon --pid <pid> [--process | --thread <tid>] ... [OPTIONS] ", options );
    }
}
