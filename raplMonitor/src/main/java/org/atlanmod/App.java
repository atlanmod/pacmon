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
        String sPathRes = "./raplMonitor/src/main/resources/resultatTER.txt";
        try {

            Files.delete(Paths.get(sPathRes));


            Files.createFile(Paths.get(sPathRes));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        GlobalBufferWriter.getInstance("./raplMonitor/src/main/resources/resultatTER.txt");
        double total = 0;
        for(int i = 0; i < 100; i++) {
            double timestart = System.nanoTime();
            double start = RAPLMonitor.getEnergy();
            double number = Math.pow(10, 7); // To change the number of Throws
            double res = computePI(number);
            double end = RAPLMonitor.getEnergy();
            double timeend = System.nanoTime();
          //  System.out.println("Résultat de PI : "+res);
            double energy = end-start;
            double time = timeend-timestart;
           // System.out.println("Energie consommée (J): "+energy/1000000);
           // System.out.println("Temps : "+time);
            double puissance = (energy*1000) / time;
           // System.out.println("Puissance : "+puissance);
            total +=(energy/1000000);
        }
        try {
            GlobalBufferWriter.bw.write("Moyenne de l'energie en watt : "+total/100+"  ");
            GlobalBufferWriter.bw.close();
            GlobalBufferWriter.fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;


        try {
            BufferedReader reader = new BufferedReader(new FileReader("./raplMonitor/src/main/resources/resultatTER.txt"));
        } catch (FileNotFoundException e) {
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
