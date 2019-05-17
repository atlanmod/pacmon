package org.atlanmod.metrics;

import org.atlanmod.*;
import org.powerapi.core.LinuxHelper;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PowerApiMonteCarlo implements BenchmarkMetrics {

    private MonitorBuilder monitorBuilder;
    private Thread thread;
    private int pid;
    private String fileOutputPath;

    /**
     * Constructor of a BenchmarkMetrics that measures the power used
     * by the Monte Carlo method to compute pi.
     * @param numThrows The number of throws that will be done to compute pi
     * @param withThreadLevel If this parameter is set to true, the power
     *                        measures will be done at a thread level (with
     *                        pacman). If it is set to false the measures will
     *                        be done at a process level (with PowerAPI).
     */
    public PowerApiMonteCarlo(int numThrows, boolean withThreadLevel) {
        monitorBuilder = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(20, TimeUnit.MILLISECONDS)
                .withTdp(15)
                .withTdpFactor(0.7);

        pid = (int) SystemUtils.getPID();

        thread = new Thread(() -> {
            Thread.currentThread().setName("MY BIG PHAT THREAD ");

            System.out.println("Thread started: " + Thread.currentThread().getId());

            // -------------
            double number = Math.pow(10, numThrows); // To change the number of Throws
            double res = computePI(number);
            System.out.println("résultat:" + res);
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

    // Calculates PI based on the number of throws versus misses
    public double computePI (double numThrows) {
        Random randomGen = new Random(System.currentTimeMillis());
        double hits = 0;
        double PI = 0;

        for (double i = 1; i <= numThrows; i++)
        {
            // Create a random coordinate result to test
            double xPos = (randomGen.nextDouble()) * 2 - 1.0;
            double yPos = (randomGen.nextDouble()) * 2 - 1.0;

            // Was the coordinate hitting the dart board?
            if (isInside(xPos, yPos))
            {
                hits++;
            }
        }

        // Use Monte Carlo method formula
        PI = (4.0 * (hits/ (double) numThrows));

        return PI;
    }

    public boolean isInside (double xPos, double yPos) {
        double distance = Math.sqrt((xPos * xPos) + (yPos * yPos));

        return (distance < 1.0);
    }
}
