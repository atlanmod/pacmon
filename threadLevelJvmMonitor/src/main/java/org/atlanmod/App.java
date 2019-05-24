package org.atlanmod;

import org.atlanmod.module.ThreadModule;
import org.atlanmod.reporter.PacmonChartDisplay;
import org.powerapi.PowerDisplay;
import org.powerapi.core.LinuxHelper;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class App {

    public App() {
    }

    public void run() {
        MonitorBuilder monitorBuilder = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(500, TimeUnit.NANOSECONDS)
                .withTdp(15)
                .withTdpFactor(0.7);

        int pid = (int) SystemUtils.getPID();

        Thread t1 = new Thread(() -> {
            Thread.currentThread().setName("Massive thread");

            System.out.println("Thread started: " + Thread.currentThread().getId());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // -------------
            double number = Math.pow(10, 8); // To change the number of Throws
            double res = computePI(number);
            System.out.println("résultat:" + res);
            // -------------
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("done");
            System.exit(1);
        });

        int tid = (int) t1.getId();
        t1.start();

        Monitor monitor = monitorBuilder
                .withModule(new ThreadModule(new LinuxHelper(), 15d, 0.7d, tid))
                .withCustomDisplay(new PacmonChartDisplay())
                .build();
        monitor.run(pid);

        while (t1.isAlive()) ;
        monitor.stop();
    }

    public static void main(String[] args) throws Exception {
        new App().run();
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
