package org.atlanmod.metrics;

/**
 * Implement this interface with code that has to be
 * measured by the benchmark app
 */
public interface BenchmarkMetrics {

    /**
     * Method use to execute the code that is measured
     * @return The path of the output file with the metrics
     */
    public String run();

}
