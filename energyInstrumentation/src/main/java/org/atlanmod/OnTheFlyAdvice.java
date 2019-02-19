package org.atlanmod;

import net.bytebuddy.asm.Advice;
import org.powerapi.PowerDisplay;
import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import scala.collection.immutable.Set;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class OnTheFlyAdvice {

    @Advice.OnMethodEnter
    static void enter(@Advice.Local("monitor") Monitor monitor) {
        PowerDisplay display = new PowerDisplay() {
            @Override
            public void display(UUID muid, long timestamp, Set<Target> targets, Set<String> devices, Power power) {
                System.out.print(timestamp + " : ");
                System.out.println(power.toMilliWatts());
            }
        };
        monitor = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(100, TimeUnit.MILLISECONDS)
                .withTdp(15)
                .withTdpFactor(0.7)
                .withCustomDisplay(display)
                .build();
        monitor.run((int) SystemUtils.getPID());
    }

    @Advice.OnMethodExit
    static void exit(@Advice.Local("monitor") Monitor monitor) {

        monitor.stop();
    }
}