package org.atlanmod;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * Main class for running benchmarks on non-instrumentable programs
 */
public class App 
{
    public static void main( String[] args ) throws IOException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        //TODO Pass the JAR to execute as a parameter , and use that parameter in the ProcessBuilder instead. That would be more maintainable

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "energyInstrumentation/src/main/resources/processBenchmark-1.0-SNAPSHOT.jar");

        //Redirection of the process output to the standard IO
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = processBuilder.start();


        //get the pid field of the process through reflexion (The PID is private, the class definition has to be changed at runtime to access it)
        Field f = process.getClass().getDeclaredField("pid");
        f.setAccessible(true);
        int pid = (int) f.get(process);

        Monitor monitor = new MonitorBuilder()
                .withChartDisplay()
                .withTdp(15)
                .withTdpFactor(.7)
                .withRefreshFrequency(1, TimeUnit.MILLISECONDS)
                .withDuration(Integer.MAX_VALUE, TimeUnit.SECONDS)
                .build();

        System.out.println(pid+" now running");

        monitor.run(pid);

        while (process.isAlive()) {
            Thread.sleep(500);
        }

        monitor.stop();

        System.out.println("program stopped running");
    }
}
