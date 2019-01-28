package com.tblf;

import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import org.powerapi.reporter.ConsoleDisplay;
import scala.collection.immutable.Set;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Hello world!
 *
 */
public class App2
{
    public static void main( String[] args ) throws InterruptedException {
        final NavigableMap<Long, Double> map = new TreeMap<>();

        Monitor monitor = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(100, TimeUnit.MILLISECONDS)
                .withTdp(15)
                .withTdpFactor(0.7)
                .withChartDisplay()
                .build();

        monitor.run((int) SystemUtils.getPID());

        Thread.sleep(10000);

        long k = 0;

        for (long i = 0; i < 50000000000L; ++i) {
            k++;
        }

        Thread.sleep(5000);
        monitor.stop();


    }

}
