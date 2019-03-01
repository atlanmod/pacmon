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

This Builder creates a `Monitor`, lasting for 60seconds, measuring the power every 100ms, displaying the results in a standard JChart console. 
The `tdp` "Thermal Dissipation Power" is an information depending on your CPU, and obtainable in its characteristics. 
Instead of a `withChartDisplay`, the `withConsoleDisplay` is possible, printing the results in the console. 
Finally the `withCustomDisplay` enables the usage of custom tools, in order to redirect the power values to different outputs, for further usages. 
/!\ About `CustomDisplay`s : Modern IDEs might propose you to declare those as anonymous functions. *DON'T*! The Actor's names in Akka are automatically generated through the class names of the Displays. An anonymous function will result in a disfunction of the Monitor.

To run the built `Monitor`, simply call `monitor.run(pid);`


