package atlanmod;

import org.atlanmod.Monitor;
import org.atlanmod.MonitorBuilder;
import org.atlanmod.SystemUtils;
import org.atlanmod.module.ThreadModule;
import org.junit.Test;
import org.powerapi.core.LinuxHelper;

import java.util.concurrent.TimeUnit;

public class ThreadMonitorTest {

    @Test
    public void checkStartMonitor() throws InterruptedException {
        MonitorBuilder monitorBuilder = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(10, TimeUnit.MILLISECONDS)
                .withTdp(15)
                .withTdpFactor(0.7)
                .withChartDisplay();



        int pid = (int) SystemUtils.getPID();

        Thread t1 = new Thread(() -> {
            Thread.currentThread().setName("MY BIG PHAT THREAD ");

            System.out.println("Thread started: "+Thread.currentThread().getId());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(long i = 0L; i < 99999999999L; ++i) {
                ++i;
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("done");
            System.exit(1);
        });

        int tid = (int) t1.getId();
        t1.start();

        Monitor monitor = monitorBuilder.withModule(new ThreadModule(new LinuxHelper(), 15d, 1d, tid)).build();
        monitor.run(pid);

        while (t1.isAlive());
        monitor.stop();
    }
}
