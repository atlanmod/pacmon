# PACMON  ![](https://travis-ci.org/atlanmod/pacmon.svg?branch=master)
PACMON is an extension of **[PowerApi](https://github.com/powerapi-ng/powerapi-scala)**. PACMON allows to easily measure the energy consumed by a Java application at the thread-level. 

## Instrumentation
The source code is instrumented using **[ByteBuddy](https://github.com/raphw/byte-buddy)**. The main method of the application is instrumented to send a start signal at its beginning and a stop signal at its end to a server based monitor. 
All the methods of the application are also instrumented to send their begin and end timestamps to the server. 

## Use case
To start the server, simply run the main method of pacmon/jvmMonitor/src/main/java/org/atlanmod/HttpBasedMonitor.java .
Once the server is running, instrument the source code using :

`java -javaagent:target/Jar_Absolute_Path=Jar_Package_Name,Name_Of_The_Main_Method,Level_Of_Accuracy -jar Jar_Absolute_Path`

Level_Of_Accuracy can either be ***process*** or ***thread***, depending on the desired level of accuracy.

For exemple:

`java -javaagent:target/energyInstrumentation-1.0-SNAPSHOT-jar-with-dependencies.jar=benchmark,main,thread -jar target/energyInstrumentation-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Power Model

Considering PowerAPI power model we determine the following formula:

![formulae](https://user-images.githubusercontent.com/6909730/58875234-43790b00-86cb-11e9-82fd-81a9ef1b04ed.png)

From this formula, we deduce the following formula for estimating the thread level energy consumption:

![formula2](https://user-images.githubusercontent.com/6909730/58875249-4ecc3680-86cb-11e9-8daa-3986ed72d192.png)

![formula3](https://user-images.githubusercontent.com/6909730/58875273-5ee41600-86cb-11e9-9daa-13fdde2560af.png)

## Evaluation

We compared Pacmon to jRAPL and PowerAPI (SimpleCPUModule):

![chart1](https://user-images.githubusercontent.com/6909730/58875264-58559e80-86cb-11e9-9df1-e7a5a3d6250a.png)

![chart2](https://user-images.githubusercontent.com/6909730/58875270-5be92580-86cb-11e9-8f63-4a235be57abf.png)

