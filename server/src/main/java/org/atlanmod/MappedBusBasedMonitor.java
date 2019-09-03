package org.atlanmod;

import io.mappedbus.MappedBusReader;
import io.mappedbus.MappedBusWriter;
import org.apache.commons.io.IOUtils;
import org.atlanmod.module.ThreadModule;
import org.powerapi.PowerDisplay;
import org.powerapi.core.LinuxHelper;
import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import scala.collection.immutable.Set;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MappedBusBasedMonitor implements Runnable {

    private File repo;
    private int pid;
    private Monitor monitor;

    /**
     *
     * @param args Arguments.
     * arg[0] : pid to track
     *
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new RuntimeException("arg[0]: pid ; arg[1]: repo");
        }

        MappedBusBasedMonitor mappedBusBasedMonitor = new MappedBusBasedMonitor(new File(args[1]), Integer.parseInt(args[0]));
        mappedBusBasedMonitor.run();
    }

    public MappedBusBasedMonitor(File repo, int pid) {
        this.repo = repo;
        this.pid = pid;
    }

    @Override
    public void run() {
        try {
            monitor = buildMonitorPowerApi();
            monitor.run(pid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the monitor
     */
    public static synchronized void stop() {
        
    }


    /**
     * build a monitor using pacmon
     * @param tid the thread id observed
     * @return a {@link Monitor}
     * @throws IOException
     */
    private Monitor buildMonitorThreadLevel(String tid) throws IOException{
        MappedBusWriter mappedBusWriter = buildPowerMappedBusWriter();

        return new MonitorBuilder()
                .withCustomDisplay(buildPowerDisplay(mappedBusWriter))
                .withTdp(15)
                .withTdpFactor(0.7)
                .withRefreshFrequency(50, TimeUnit.MILLISECONDS)
                .withModule(new ThreadModule(new LinuxHelper(), 15d, 0.7d, Integer.parseInt(tid)))
                .build();
    }

    /**
     * build a monitor using powerAPI
     * @return a {@link Monitor}
     * @throws IOException
     */
    protected Monitor buildMonitorPowerApi() throws IOException {

        MappedBusWriter mappedBusWriter = buildPowerMappedBusWriter();

        return new MonitorBuilder()
                .withCustomDisplay(buildPowerDisplay(mappedBusWriter))
                .withTdp(15.0)
                .withTdpFactor(0.7)
                .withRefreshFrequency(50, TimeUnit.MILLISECONDS)
                .build();

    }

    /**
     * Creates a {@link MappedBusWriter} that writes floats to a file
     * @return a {@link MappedBusWriter}
     * @throws IOException if an error occurs while opening the output file
     */
    private MappedBusWriter buildPowerMappedBusWriter() throws IOException {
        if (repo != null && !repo.exists())
            repo.mkdirs();

        File output = File.createTempFile("power", "", repo);

        MappedBusWriter writer = new MappedBusWriter(output.getAbsolutePath(), 100000L, 32);

        try {
            writer.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

    /**
     * Build a {@link PowerDisplay} that writes to the given {@link MappedBusWriter}
     * @param mappedBusWriter the {@link MappedBusWriter}
     * @return the {@link PowerDisplay} created
     */
    private PowerDisplay buildPowerDisplay(MappedBusWriter mappedBusWriter) {
        return new PowerDisplay() { // do not collapse to lamda
            @Override
            public void display(UUID muid, long timestamp, Set<Target> targets, Set<String> devices, Power power) {
                try {
                    mappedBusWriter.write(new MappedBusFloat((float) power.toMilliWatts()));
                } catch (EOFException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
