package org.atlanmod;

import org.atlanmod.metrics.BenchmarkMetrics;
import org.atlanmod.metrics.PowerApiMonteCarlo;

public class App {

    public static void main(String[] args) {
        BenchmarkMetrics benchmarkMetrics = new PowerApiMonteCarlo(8, false);
        System.out.println(" -- Starting benchmark --");
        String filePath = benchmarkMetrics.run();
        System.out.println(" -- Starting energy computing --");
        new EnergyCalculator().run(filePath);
    }

}
