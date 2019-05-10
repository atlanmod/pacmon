package org.atlanmod;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        String sPathValue = "./raplMonitor/src/main/resources/values.txt";
        try {

            Files.delete(Paths.get(sPathValue));


            Files.createFile(Paths.get(sPathValue));
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        GlobalBufferWriter.getInstance("./raplMonitor/src/main/resources/values.txt");
        double total = 0;
        for(int i = 0; i < 100; i++) {
            double timestart = System.nanoTime();
            double start = RAPLMonitor.getEnergy();
            double number = Math.pow(10, 1); // To change the number of Throws
            double res = computePI(number);
            double end = RAPLMonitor.getEnergy();
            double timeend = System.nanoTime();
            //  System.out.println("Résultat de PI : "+res);
            double energy = end-start;
            try {
                GlobalBufferWriter.bw.write(String.valueOf(energy)+"/");
            } catch (IOException e) {
                e.printStackTrace();
            }
            double time = timeend-timestart;
            // System.out.println("Energie consommée (J): "+energy/1000000);
            // System.out.println("Temps : "+time);
            double puissance = (energy*1000) / time;
            // System.out.println("Puissance : "+puissance);
            total +=(energy/1000000);
        }

        try {


            //  GlobalBufferWriter.bw.write(String.valueOf(total/100));
            GlobalBufferWriter.bw.close();
            GlobalBufferWriter.fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;


        try {
            BufferedReader reader = new BufferedReader(new FileReader("./raplMonitor/src/main/resources/values.txt"));
            String values = org.apache.commons.io.IOUtils.toString(reader);
            String correctedValues = values.replaceAll("/0.0", "");
            if(Character.toString(correctedValues.charAt(0)).equals("0")) {
                correctedValues = correctedValues.substring(4);
            }

            String[] array = correctedValues.split("/");
            Double[] arrayDouble = new Double[array.length];
            for(int i = 0; i < array.length; i++) {

                arrayDouble[i] = Double.parseDouble(array[i]);

            }
            double res = 0;
            double variance = 0;
            for(int i = 0; i < arrayDouble.length; i++) {
                res += arrayDouble[i];
            }
            double moyenne = res / arrayDouble.length;
            for(int i = 0; i < arrayDouble.length; i++) {
                variance += Math.pow((arrayDouble[i] - moyenne), 2);
            }
            variance = variance / arrayDouble.length;
            double ecarttype = Math.sqrt(variance);
            System.out.println("Moyenne : "+moyenne/1000000);
            System.out.println("Ecart-type : "+ecarttype/1000000);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // Calculates PI based on the number of throws versus misses
    public static double computePI (double numThrows) {
        Random randomGen = new Random(System.currentTimeMillis());
        double hits = 0;
        double PI = 0;

        for (double i = 1; i <= numThrows; i++)
        {
            // Create a random coordinate result to test
            double xPos = (randomGen.nextDouble()) * 2 - 1.0;
            double yPos = (randomGen.nextDouble()) * 2 - 1.0;

            // Was the coordinate hitting the dart board?
            if (isInside(xPos, yPos))
            {
                hits++;
            }
        }

        // Use Monte Carlo method formula
        PI = (4.0 * (hits/ (double) numThrows));

        return PI;
    }

    public static boolean isInside (double xPos, double yPos) {
        double distance = Math.sqrt((xPos * xPos) + (yPos * yPos));

        return (distance < 1.0);
    }
}
