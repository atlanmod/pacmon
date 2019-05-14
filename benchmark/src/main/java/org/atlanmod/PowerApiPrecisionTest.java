package org.atlanmod.metrics;
import org.atlanmod.*;

import java.util.concurrent.TimeUnit;

public class PowerApiPrecisionTest {
    static int freqMaxForTest = 200;
    static int stepfreq = 10;

    public static void main(String args[]){
        // création du moniteur de test
        double[] results = new double[freqMaxForTest/10];
        double[][] finalRes = new double[freqMaxForTest/5][2];

        for (int frequence=freqMaxForTest; frequence >= 0; frequence -= stepfreq) {
            // test pour 20 itérations
            for (int i = 0; i < 20; i++) {
                PowerApiMonteCarlo pm = new PowerApiMonteCarlo(7, false);
                pm.setMonitorFrequency(frequence, TimeUnit.MILLISECONDS);
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
            finalRes[frequence/stepfreq][0] = frequence;
            finalRes[frequence/stepfreq][1] = ecarttype/1000;
        }
        for (int i=0; i<=freqMaxForTest/stepfreq;i++){
            System.out.println("----------------------------------------------------------");
            System.out.println("Ecart-type pour frequence,"+finalRes[i][0]+" : "+finalRes[i][1]);
        }
    }
}