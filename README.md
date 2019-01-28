# jPowerApi
Java Facade for running PowerAPI. For more information about powerAPI, check their repo: 
https://github.com/Spirals-Team/powerapi

jPowerAPI is a facade for the original Scala PowerAPI project. It's purpose is to get rid of the configuration file necessary for PowerAPI, and/or the Scala syntax, especially when working from Java. 

## Installation

jPowerAPI is a simple maven project. Just import it in your favorite IDE and run a `mvn package` to build the project.

## Execution

This project propose a builder: `MonitorBuilder` creating a measure, used to monitor the power at specific frequencies.
```
Monitor monitor = new MonitorBuilder()
        .withDuration(60, TimeUnit.SECONDS)
        .withRefreshFrequency(100, TimeUnit.MILLISECONDS)
        .withTdp(15)
        .withTdpFactor(0.7)
        .withChartDisplay()
        .build();
                
```

This Builder creates a `Monitor`, lasting for 60seconds, measuring the power every 100ms, displaying the results in a standard JChart console. The `tdp` "Thermal Dissipation Power" is an information depending on your CPU, and obtainable in its characteristics. 

To run the built `Monitor`, simply call `monitor.run(pid);`
