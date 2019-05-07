package org.atlanmod;

import org.junit.Test;
import org.powerapi.PowerDisplay;
import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import org.powerapi.module.PowerChannel;
import scala.collection.immutable.Set;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class MonitorBuilderTest
{

    @Test
    public void checkPowerAPIMonitorChartDisplay() throws InterruptedException {

        Monitor monitor = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(100, TimeUnit.MILLISECONDS)
                .withTdp(15)
                .withTdpFactor(0.7)
                .withChartDisplay()
                .build();

        monitor.run((int) SystemUtils.getPID());

        Thread.sleep(1000);

        long k = 0;

        for (long i = 0; i < 5000000L; ++i) {
            k++;
        }

        Thread.sleep(1000);
        monitor.stop();

    }

    @Test
    public void checkPowerAPIMonitorConsoleDisplay() throws InterruptedException {

        Monitor monitor = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(100, TimeUnit.MILLISECONDS)
                .withTdp(15)
                .withTdpFactor(0.7)
                .withConsoleDisplay()
                .build();

        monitor.run((int) SystemUtils.getPID());

        Thread.sleep(1000);

        long k = 0;

        for (long i = 0; i < 5000000L; ++i) {
            k++;
        }

        Thread.sleep(1000);
        monitor.stop();
    }

    @Test
    public void checkPowerAPIMonitorCustomDisplay() throws InterruptedException {

        Map<Power, Long> powers = new HashMap<>();

        Monitor monitor = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(100, TimeUnit.MILLISECONDS)
                .withTdp(15)
                .withTdpFactor(0.7)
                .withCustomDisplay(new PowerDisplay() {
                    //DO NOT REDUCE TO LAMBDA EXPRESSION
                    public void display(UUID muid, long timestamp, Set<Target> targets, Set<String> devices, Power power) {
                        powers.put(power, timestamp);
                    }
                })
                .build();

        monitor.run((int) SystemUtils.getPID());

        Thread.sleep(1000);

        long k = 0;

        for (long i = 0; i < 5000000L; ++i) {
            k++;
        }

        Thread.sleep(1000);

        monitor.stop();

        Double averagePower = powers.keySet().stream().map(Power::toWatts).mapToDouble(d -> d).average().orElse(0); //Average power consumpion during loop execution
        double duration = ((Collections.max(powers.values()) - Collections.min(powers.values())) / 1000D);

        System.out.println("Average Power consumed (W): "+averagePower);
        System.out.println("Duration (s): "+duration);
        System.out.println("Energy Consumed (J): "+duration*averagePower);

    }
}
