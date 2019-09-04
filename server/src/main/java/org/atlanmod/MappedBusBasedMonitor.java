package org.atlanmod;

import io.mappedbus.MappedBusWriter;
import org.atlanmod.module.ThreadModule;
import org.powerapi.PowerDisplay;
import org.powerapi.core.LinuxHelper;
import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import scala.collection.immutable.Set;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MappedBusBasedMonitor implements Runnable {

    private File repo;
    private int pid;
    private Monitor monitor;

    public MappedBusBasedMonitor(File repo, int pid) {
        this.repo = repo;
        this.pid = pid;
    }

    @Override
    public void run() {
        monitor.run(pid);
    }

    /**
     * build a monitor using pacmon
     * @param tid the thread id observed
     * @throws IOException
     */
    public void buildMonitorThreadLevel(int tid) throws IOException{
        MappedBusWriter mappedBusWriter = buildPowerMappedBusWriter();

        monitor = new MonitorBuilder()
                .withCustomDisplay(buildPowerDisplay(mappedBusWriter))
                .withTdp(15)
                .withTdpFactor(0.7)
                .withRefreshFrequency(50, TimeUnit.MILLISECONDS)
                .withModule(new ThreadModule(new LinuxHelper(), 15d, 0.7d, tid))
                .build();
    }

    /**
     * build a monitor using powerAPI
     * @return a {@link Monitor}
     */
    public void buildMonitorPowerApi() throws IOException {
        MappedBusWriter mappedBusWriter = buildPowerMappedBusWriter();
        monitor = new MonitorBuilder()
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

        MappedBusWriter writer = new MappedBusWriter(output.getAbsolutePath(), 200000L, 32);

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
                    System.out.println("power: "+power);
                    mappedBusWriter.write(new MappedBusFloat((float) power.toMilliWatts()));
                } catch (EOFException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
