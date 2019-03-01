package org.atlanmod;

import org.apache.commons.lang.UnhandledException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws Exception
    {

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "energyInstrumentation/src/main/resources/processBenchmark-1.0-SNAPSHOT.jar");

        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        //get the input stream of the process to displayit's pid and check we've got the good one

        Process process = processBuilder.start();
        //get the pid field of the process
        Field f = process.getClass().getDeclaredField("pid");
        f.setAccessible(true);
        int pid = (int) f.get(process);

        System.out.println(pid+" now running");




        /*

        Thread.sleep(10000);

        long k = 0;

        for (long i = 0; i < 50000000000L; ++i) {
            k++;
        }

        Thread.sleep(5000);*/


    }
}