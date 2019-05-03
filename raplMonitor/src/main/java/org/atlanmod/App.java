package org.atlanmod;

import java.util.Random;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        double timestart = System.nanoTime();
        double start = RAPLMonitor.getEnergy();
        double number = Math.pow(10, 10); // To change the number of Throws
        double res = computePI(number);
        double end = RAPLMonitor.getEnergy();
        double timeend = System.nanoTime();
        System.out.println("Résultat de PI : "+res);
        double energy = end-start;
        double time = timeend-timestart;
        System.out.println("Energie consommée (J): "+energy/1000000);
        System.out.println("Temps : "+time);
        double puissance = (energy*1000) / time;
        System.out.println("Puissance : "+puissance);

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
