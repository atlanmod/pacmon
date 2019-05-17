package org.atlanmod.metrics;

import org.atlanmod.Monitor;
import org.atlanmod.MonitorBuilder;
import org.atlanmod.PacmanPowerDisplay;
import org.atlanmod.SystemUtils;

import java.util.concurrent.TimeUnit;

public class PowerApiSleepLoop implements BenchmarkMetrics {

    private MonitorBuilder monitorBuilder;
    private Thread thread;
    private int pid;
    private String fileOutputPath;

    /**
     * Constructor of a BenchmarkMetrics that measures the power used
     * by an algorithm with sleeps and loops.
     * @param withThreadLevel If this parameter is set to true, the power
     *                        measures will be done at a thread level (with
     *                        pacman). If it is set to false the measures will
     *                        be done at a process level (with PowerAPI).
     */
    public PowerApiSleepLoop(boolean withThreadLevel) {
        monitorBuilder = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(50, TimeUnit.MILLISECONDS)
                .withTdp(15)
                .withTdpFactor(0.7);

        pid = (int) SystemUtils.getPID();

        thread = new Thread(() -> {
            Thread.currentThread().setName("MY BIG PHAT THREAD ");

            System.out.println("Thread started: " + Thread.currentThread().getId());

            // -------------
            for (int i=0;i<10;i++){
                sleepMethod();
            }
            // -------------.

            System.out.println("done");
            //Thread.currentThread().interrupted();
            //System.exit(1);
            Thread.currentThread().stop();
        });

        int tid = (int) thread.getId();

        PacmanPowerDisplay display = new PacmanPowerDisplay();
        fileOutputPath = display.getFileOutputPath();

        //TODO :  Find a way to use ThreadModule
        if (withThreadLevel) {
            //monitorBuilder = monitorBuilder.withModule(new ThreadModule(new LinuxHelper(), 15d, 0.7d, tid));
        }
        monitorBuilder = monitorBuilder
                //.withChartDisplay()
                .withCustomDisplay(display)
        ;
    }

    public void setMonitorFrequency(long frequency, TimeUnit unit) {
        monitorBuilder.withRefreshFrequency(frequency, unit);
    }

    public String run() {
        Monitor monitor = monitorBuilder.build();
        thread.start();
        monitor.run(pid);
        while (thread.isAlive());
        monitor.stop();
        return fileOutputPath;
    }

    public void sleepMethod(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("I'm awake");
        addMethod();
        //System.out.println("I finished adding");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("byebye");
    }

    public void addMethod(){
        for(long i = 0L; i < 999999999L; ++i) {
            ++i;
        }
    }

}
