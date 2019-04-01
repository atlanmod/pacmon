package org.atlanmod;

import io.javalin.Javalin;
import org.apache.http.HttpStatus;

import java.util.concurrent.TimeUnit;

public class HttpBasedMonitor {
    private static Monitor monitor;

    public static void main(String[] args) {

        monitor = buildMonitor();
        Javalin javalin = Javalin.create();

        javalin.post("/start", ctx -> {
            monitor.run(Integer.parseInt(ctx.req.getParameter("pid")));
            ctx.status(HttpStatus.SC_ACCEPTED);
        });

        javalin.post("/stop", ctx -> {
            monitor.stop();
            ctx.status(HttpStatus.SC_ACCEPTED);
            monitor = buildMonitor();
        });

        javalin.start(7070);

    }

    public static Monitor buildMonitor() {
        return new MonitorBuilder()
                .withChartDisplay()
                .withTdp(15.0)
                .withTdpFactor(0.7)
                .withRefreshFrequency(10, TimeUnit.NANOSECONDS)
                .build();
    }
}
