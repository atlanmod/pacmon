package org.atlanmod;

import org.atlanmod.module.ThreadModule;
import org.atlanmod.reporter.PacmonChartDisplay;
import org.junit.Test;
import org.powerapi.PowerDisplay;
import org.powerapi.core.LinuxHelper;
import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import scala.collection.immutable.Set;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ThreadMonitorTest {

    @Test
    public void checkStartMonitor() throws InterruptedException {
        MonitorBuilder monitorBuilder = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(10, TimeUnit.MILLISECONDS)
                .withTdp(15)
                .withTdpFactor(0.7)
                .withCustomDisplay(new PowerDisplay() {
                    @Override
                    public void display(UUID muid, long timestamp, Set<Target> targets, Set<String> devices, Power power) {

                    }
                });
        int pid = (int) SystemUtils.getPID();

        Thread t1 = new Thread(() -> {
            Thread.currentThread().setName("MY BIG PHAT THREAD ");

            System.out.println("Thread started: "+Thread.currentThread().getId());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(long i = 0L; i < 99999999L; ++i) {
                ++i;
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("done");
        });

        int tid = (int) t1.getId();

        Monitor monitor = monitorBuilder.withModule(new ThreadModule(new LinuxHelper(), 15d, 0.7d, tid)).build();
        monitor.run(pid);
        t1.start();

        while (t1.isAlive());

        monitor.stop();
    }
}
