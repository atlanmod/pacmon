package org.atlanmod;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.javalin.Javalin;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.atlanmod.module.ThreadModule;
import org.powerapi.PowerDisplay;
import org.powerapi.core.LinuxHelper;
import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import scala.collection.immutable.Set;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HttpBasedMonitor {
    private static Monitor monitorPowerApi;
    private static Monitor monitorThreadLevel;
    private static File file;
    private static File metrics;
    private static File timeStamps;
    private static FileOutputStream fileOutputStreamMetrics;
    private static FileOutputStream fileOutputStreamTimeStamp;
    private static FileOutputStream fileOutputStreamResults;
    private static Javalin javalin;

    public static void main(String[] args) throws IOException {

        javalin = Javalin.create();

        /*
        starts a monitor using powerAPI
         */
        javalin.post("/startpowerapi", ctx -> {
            if (monitorPowerApi != null)
                throw new Exception("A monitor is already running. Interruption.");

            int pid = Integer.parseInt(ctx.req.getParameter("pid"));
            String repo = ctx.req.getParameter("uri");

            if (repo == null) {
                file = new File("trace");
            } else {
                file = new File(repo);
            }

            monitorPowerApi = buildMonitorPowerApi();
            monitorPowerApi.run(pid);
            ctx.status(HttpStatus.SC_ACCEPTED);
        });

        javalin.post("/startthreadlevel", ctx -> {
            System.out.println("Received start signal.");
            monitorThreadLevel = buildMonitorThreadLevel(ctx.req.getParameter("tid"));
            monitorThreadLevel.run(Integer.parseInt(ctx.req.getParameter("pid")));
            ctx.status(HttpStatus.SC_ACCEPTED);
        });

        /*
        stops the monitor and interprets the results
         */
        javalin.post("/stoppowerapi", ctx -> {

            monitorPowerApi.stop();
            monitorPowerApi = null;
            System.out.println("Received stop signal.");
            ctx.status(HttpStatus.SC_ACCEPTED);
            interpretTrace();

            fileOutputStreamTimeStamp.close();
            fileOutputStreamMetrics.close();
        });

        javalin.post("/stopthreadlevel", ctx -> {
            monitorThreadLevel.stop();
            System.out.println("Received stop signal.");
            ctx.status(HttpStatus.SC_ACCEPTED);
            interpretTrace();

            fileOutputStreamTimeStamp.close();
            fileOutputStreamMetrics.close();
        });

        /*
        records a methods start timestamp
         */
        javalin.post("/begintime", ctx -> {
            System.out.println("Received start timestamp signal.");
            try {
                IOUtils.write("Start "+ctx.req.getParameter("name")+" "+ctx.req.getParameter("timestamp")+"\n", fileOutputStreamTimeStamp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /*
        records a method stop timestamp
         */
        javalin.post("/endtime", ctx -> {
            try {
                IOUtils.write("End "+ctx.req.getParameter("name")+" "+ctx.req.getParameter("timestamp")+"\n", fileOutputStreamTimeStamp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        javalin.get("/ready", ctx -> {
            ctx.status(201);
        });

        javalin.post("/stop", ctx -> {
            new Thread(new StopServer()).start();
            ctx.status(200);
        });

        javalin.start(7070);
    }

    static class StopServer implements Runnable  {
        @Override
        public void run() {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            javalin.stop();
        }
    }

    private static Monitor buildMonitorThreadLevel(String tid) throws IOException{

        if (!file.exists())
            file.mkdirs();

        metrics = File.createTempFile("metrics"+String.valueOf(System.currentTimeMillis()), ".txt", file);
        fileOutputStreamMetrics = new FileOutputStream(metrics);

        timeStamps = File.createTempFile("timestamps"+String.valueOf(System.currentTimeMillis()), ".txt", file);
        fileOutputStreamTimeStamp = new FileOutputStream(timeStamps);

        //TODO: Change writing system to binary

        org.atlanmod.Monitor monitor = new MonitorBuilder()
                .withCustomDisplay(new PowerDisplay() {
                    @Override
                    public void display(UUID muid, long timestamp, Set<Target> targets, Set<String> devices, Power power) {
                        try {
                            IOUtils.write(String.valueOf(timestamp).concat(":").concat(String.valueOf(power.toMilliWatts())).concat("\n"), fileOutputStreamMetrics);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .withTdp(15)
                .withTdpFactor(0.7)
                .withRefreshFrequency(50, TimeUnit.MILLISECONDS)
                .withModule(new ThreadModule(new LinuxHelper(), 15d, 0.7d, Integer.valueOf(tid)))
                .build();

        return monitor;
    }

    /**
     * build a monitor using powerAPI
     * @return
     * @throws IOException
     */
    private static Monitor buildMonitorPowerApi() throws IOException {

        if (file != null && !file.exists())
            file.mkdirs();

        metrics = File.createTempFile("metrics"+String.valueOf(System.currentTimeMillis()), ".txt", file);
        fileOutputStreamMetrics = new FileOutputStream(metrics);

        timeStamps = File.createTempFile("timeStamps"+String.valueOf(System.currentTimeMillis()), ".txt", file);
        fileOutputStreamTimeStamp = new FileOutputStream(timeStamps);

        //TODO: Change writing system to binary

        return new MonitorBuilder()
                .withCustomDisplay(new PowerDisplay() {
                    @Override
                    public void display(UUID muid, long timestamp, Set<Target> targets, Set<String> devices, Power power) {
                        try {
                            IOUtils.write(String.valueOf(timestamp)+":"+String.valueOf(power.toMilliWatts())+"\n", fileOutputStreamMetrics);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .withTdp(15.0)
                .withTdpFactor(0.7)
                .withRefreshFrequency(50, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * This method is used to interpret the trace files created by monitoring the execution of a jar file
     */
    private static void interpretTrace(){

        FileReader fileReader = null;
        try {
            File results = File.createTempFile("results" + String.valueOf(System.currentTimeMillis()), ".txt", file);
            fileOutputStreamResults = new FileOutputStream(results);
            fileReader = new FileReader(timeStamps);

            LineNumberReader bufferedReader = new LineNumberReader(fileReader);

            //this line contains the start of the first method
            String s1 = bufferedReader.readLine();

            //we parse the entire timestamp file
            while(s1!=null){

                //we are looking for the end of the method, but we might need to go back to the start of this method
                //if the method contains calls to other methods
                bufferedReader.mark(0);

                String s2 = bufferedReader.readLine();

                String[] splitedS2 = s2.split("\\s+");
                String[] splitedS1 = s1.split("\\s+");

                //while the line doesnt match the end of the current method, we read the next line
                while (!(splitedS2[0].equals("End") && splitedS2[1].equals(splitedS1[1]))){

                    s2 = bufferedReader.readLine();
                    splitedS2 = s2.split("\\s+");

                }

                String beginTimeStamp = splitedS1[2]; //the timestamp of the start of the method
                String endTimeStamp = splitedS2[2]; //the timestamp of the end of the method
                String methodName = splitedS1[1]; //the name of the current method

                computeEnergy(beginTimeStamp, endTimeStamp, methodName);

                bufferedReader.reset(); //the parser goes back to the line containing the start of the current method

                s1 = bufferedReader.readLine(); //and we continue reading the file
                splitedS1 = s1.split("\\s+");

                //while the line isnt describing the start of a method (or we are at the end of the file), we read another line
                while( !splitedS1[0].equals("Start") && s1!=null ){
                    s1 = bufferedReader.readLine();
                    if (s1!=null){
                        splitedS1 = s1.split("\\s+");
                    }
                }

            }
            bufferedReader.close();
            fileOutputStreamResults.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * this method allows to compute the energy used between two timestamps
     * @param beginTimeStamp
     * @param endTimeStamp
     * @param methodName
     */
    private static void computeEnergy(String beginTimeStamp, String endTimeStamp, String methodName){
        try {
            FileReader fileReader = new FileReader(metrics);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            double energy = 0.0;
            String s1 = bufferedReader.readLine();

            //we ignore lines until we reach a line that contains a timestamp that takes place after the start of the method
            while(getTime(s1) < Long.parseLong(beginTimeStamp)){
                s1 = bufferedReader.readLine();
            }

            String s2 = bufferedReader.readLine();

            //until we reach a line that contains a timestamps that takes place after the end of the method, we compute the power used
            while (s2 != null && getTime(s2) < Long.parseLong(endTimeStamp)) {
                long time = getTime(s2) - getTime(s1);
                energy += powerToEnergy(getPower(s2),time);
                s1 = s2;
                s2 = bufferedReader.readLine();
            }

            IOUtils.write(String.valueOf(methodName) + " : " + energy/1000 + "J\n", fileOutputStreamResults);
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * retrieves the time of a line
     * @param s
     * @return
     */
    private static long getTime(String s) {
        return Long.parseLong(s.split(":")[0]);
    }

    /**
     * retrieve the power of a line
     * @param s
     * @return
     */
    private static double getPower(String s) {
        return Double.parseDouble((s.split(":")[1]));
    }

    /**
     * Calculates energy (Joule) with the given power (Watt)
     * @param power Power in milliwatts as a double
     * @param time Time in milliseconds as a long
     * @return Energy in milliJoules as a double
     */
    public static double powerToEnergy(double power, long time) {
        return power*time/1000;
    }
}
