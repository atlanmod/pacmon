package org.atlanmod;

import org.atlanmod.metrics.BenchmarkMetrics;
import org.atlanmod.metrics.GlobalBufferWriter;
import org.atlanmod.metrics.PowerApiMonteCarlo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {

    public static void main(String[] args) {
        String sPathValue = "./benchmark/src/main/resources/values.txt";
        int numberOfTests = 10;
        Double[] results = new Double[numberOfTests];
        try {

            Files.delete(Paths.get(sPathValue));


            Files.createFile(Paths.get(sPathValue));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GlobalBufferWriter.getInstance("./benchmark/src/main/resources/values.txt");
        double total = 0;
        BenchmarkMetrics benchmarkMetrics = null;
        for (int i = 0; i < numberOfTests; i++) {
            benchmarkMetrics = new PowerApiMonteCarlo(8, false);

            System.out.println(" -- Starting benchmark --");
            String filePath = benchmarkMetrics.run();
            System.out.println(" -- Starting energy computing --");

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
        System.out.println("Moyenne : "+moyenne/1000);
        System.out.println("Ecart-type : "+ecarttype/1000);
    }

}
