package org.atlanmod;

import net.bytebuddy.asm.Advice;

import java.util.concurrent.TimeUnit;

public class OnTheFlyAdvice {

    @Advice.OnMethodEnter
    static void enter(@Advice.Local("monitor") Monitor monitor) {

        monitor = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(100, TimeUnit.MILLISECONDS)
                .withTdp(15)
                .withTdpFactor(0.7)
                .withChartDisplay()
                .build();

        monitor.run((int) SystemUtils.getPID());
    }

    @Advice.OnMethodExit
    static void exit(@Advice.Local("monitor") Monitor monitor) {

        monitor.stop();
    }
}