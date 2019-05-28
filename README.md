# PACMON
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
