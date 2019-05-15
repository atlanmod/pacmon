package org.atlanmod;
import org.atlanmod.*;
import org.atlanmod.metrics.PowerApiMonteCarlo;

import java.util.concurrent.TimeUnit;

public class PowerApiPrecisionTest {
    static int[] fregtToTest;
    static int nbOccurenceForTest = 20;

    public static void main(String args[]){
        // on remplit le tableau freqToTest
        int otherCpt = 0;
        for (int i=1;i<=512;i=i*2){
            otherCpt++;
        }
        fregtToTest = new int[otherCpt];
        otherCpt = 0;
        for (int i=1;i<=512;i=i*2){
            fregtToTest[otherCpt] = i;
            otherCpt++;
        }
        System.out.println(otherCpt);

        // création du moniteur de test
        double[] results = new double[nbOccurenceForTest];
        double[][] finalRes = new double[fregtToTest.length][2];
        int cptForFinalRes = 0;

        for(int freq : fregtToTest) {
            // test pour 20 itérations
            for (int i = 0; i < nbOccurenceForTest; i++) {
                PowerApiMonteCarlo pm = new PowerApiMonteCarlo(8, false);
                pm.setMonitorFrequency(freq, TimeUnit.MILLISECONDS);
                String filePath = pm.run();
                results[i] = new EnergyCalculator().run(filePath);
            }

            double res = 0;
            double variance = 0;
            for(int i = 0; i < results.length; i++) {
                res += results[i];
            }
            double moyenne = res / results.length;
            for(int i = 0; i < results.length; i++) {
                variance += Math.pow((results[i] - moyenne), 2);
            }
            variance = variance / results.length;
            double ecarttype = Math.sqrt(variance);


            finalRes[cptForFinalRes][0] = freq;
            finalRes[cptForFinalRes][1] = ecarttype/1000;
            cptForFinalRes++;
        }
        // variable pour latex
        String graph = "{";
        for (int i=0; i<finalRes.length;i++){
            System.out.println("----------------------------------------------------------");
            System.out.println("Ecart-type pour frequence,"+finalRes[i][0]+" : "+finalRes[i][1]);
            graph = graph + "("+finalRes[i][0]+","+finalRes[i][1]+") \n";
        }
        graph = graph + "};";

        System.out.println("Results.size : "+results.length);
        System.out.println("FinalRes.size : "+finalRes.length);
        System.out.println("----------------------------------------------------------");
        System.out.println(graph);

    }
}