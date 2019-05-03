package org.atlanmod;

import io.javalin.Javalin;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.powerapi.PowerDisplay;
import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import scala.collection.immutable.Set;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HttpBasedMonitor {
    private static Monitor monitor;
    private static File file;
    private static File trace;


    public static void main(String[] args) throws IOException {

        file = new File("./jvmMonitor/src/main/resources/trace");
        if(!file.exists()){
            file.mkdirs();
        }

        trace = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".txt", file);
        monitor = buildMonitor();

        Javalin javalin = Javalin.create();

        javalin.post("/start", ctx -> {
            System.out.println("Received start signal.");
            monitor.run(Integer.parseInt(ctx.req.getParameter("pid")));
            ctx.status(HttpStatus.SC_ACCEPTED);
        });

        javalin.post("/stop", ctx -> {
            monitor.stop();
            System.out.println("Received stop signal.");
            ctx.status(HttpStatus.SC_ACCEPTED);
            trace = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".txt", file);
            monitor = buildMonitor();
        });

        javalin.start(7070);

    }

    public static Monitor buildMonitor() throws IOException {

        File file = new File("./jvmMonitor/src/main/resources/trace");
        if (!file.exists())
            file.mkdirs();
        File trace = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".txt", file);
        FileOutputStream fileOutputStream = new FileOutputStream(trace);

        System.out.println("Trace writing in "+trace.getAbsolutePath());
        //TODO: Change writing system to binary

        return new MonitorBuilder()
                .withCustomDisplay(new PowerDisplay() {
                    @Override
                    public void display(UUID muid, long timestamp, Set<Target> targets, Set<String> devices, Power power) {
                        try {
                            IOUtils.write(String.valueOf(power.toMilliWatts())+"-"+String.valueOf(timestamp)+";\n", fileOutputStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .withTdp(15.0)
                .withTdpFactor(0.7)
                .withRefreshFrequency(1, TimeUnit.MILLISECONDS)
                .build();
    }
}
