package benchmark;

import java.util.Random;

public class App {

    public static void main (String[] args) {
        int numThrows = 10000000;
        double PI = computePI(numThrows);

        // Determine the difference from the PI constant defined in Math
        double Difference = PI - Math.PI;

        // Print out the total results of our trials
        System.out.println ("Number of throws = " + numThrows + ", Computed PI = " + PI + ", Difference = " + Difference );
    }

    // Determine if dart thrown is inside the dart board
    public static boolean isInside (double xPos, double yPos) {
        double distance = Math.sqrt((xPos * xPos) + (yPos * yPos));

        return (distance < 1.0);
    }

    // Calculates PI based on the number of throws versus misses
    public static double computePI (int numThrows) {
        Random randomGen = new Random(System.currentTimeMillis());
        int hits = 0;
        double PI = 0;

        for (int i = 1; i <= numThrows; i++)
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
}
