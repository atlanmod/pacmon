package org.atlanmod;

import net.bytebuddy.asm.Advice;
import org.powerapi.PowerDisplay;
import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import scala.collection.immutable.Set;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class OnTheFlyAdvice {

    //called at the beginning of the method
    @Advice.OnMethodEnter
    static void enter(@Advice.Local("monitor") Monitor monitor) {
        System.out.println("Started main method ");
        //the values and their respective timestamps measured by the mosnitor are stored in a file named outputTER
        //using this format : value-timestamp;value1-timestamp1;value2-timestamp2
        String sPath = "./energyInstrumentation/src/main/resources/outputTER.txt";
        String sPathRes = "./energyInstrumentation/src/main/resources/resultatTER.txt";
        //TODO: Improvement necessary. Why not checking if the file exist ?
        try {
            Files.delete(Paths.get(sPath));
            Files.delete(Paths.get(sPathRes));
        }
        catch (Exception e) {
        }
        try {
            Files.createFile(Paths.get(sPath));
            Files.createFile(Paths.get(sPathRes));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        GlobalBufferWriter.getInstance("./energyInstrumentation/src/main/resources/resultatTER.txt");
        PowerDisplay display = new PowerDisplay() {
            @Override
            public void display(UUID muid, long timestamp, Set<Target> targets, Set<String> devices, Power power) {

                String s = power.toMilliWatts() + "-" + timestamp + ";";
                try {
                    Files.write(Paths.get(sPath), s.getBytes(), StandardOpenOption.APPEND);
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }

        };

        monitor = new MonitorBuilder()
                .withDuration(60, TimeUnit.SECONDS)
                .withRefreshFrequency(100, TimeUnit.MILLISECONDS)
                .withTdp(47)
                .withTdpFactor(0.7)
                .withCustomDisplay(display)
                .build();

        //we start the measure
        monitor.run((int) SystemUtils.getPID());
    }

    //called at the end of the method
    @Advice.OnMethodExit
    static void exit(@Advice.Local("monitor") Monitor monitor) {
        System.out.println("Ended main method");
        //we stop the measuring
        monitor.stop();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./energyInstrumentation/src/main/resources/outputTER.txt"));

            //contains the string produced by the monitor
            String str;
            str = reader.readLine();

            //contains the strings "value-timestamp"
            String[] tokens = str.split(";");

            //will contain the values measured by the monitor
            ArrayList<String> valeursList = new ArrayList<String>();
            //will contain the timestamps of the values
            ArrayList<String> timeStampList = new ArrayList<String>();

            for (int i=0; i<tokens.length ;i++){
                String[] tmp = tokens[i].split("-");
                valeursList.add(tmp[0]);
                timeStampList.add(tmp[1]);
            }

            //converts the values from strings to long
            double valeurs[] = new double[valeursList.size()];
            for(int i=0; i < valeursList.size(); i++) {
                valeurs[i] = Double.parseDouble(valeursList.get(i));
            }

            //compute the average of the power used
            double total = 0;
            double moyenne = 0;
            for(int i=0; i < valeurs.length; i++) {
                total+=valeurs[i];
            }
            moyenne = total / valeurs.length;

            //compute the time used
            Long firstTimeStamp = Long.parseLong(timeStampList.get(0));
            Long finalTimeStamp = Long.parseLong(timeStampList.get(timeStampList.size()-1));
            Long timeSecond = (finalTimeStamp-firstTimeStamp)/1000;
            System.out.println("Moyenne de la puissance en watt : "+moyenne/1000);
            System.out.println("Temps en seconde : "+ timeSecond);
            System.out.println("Energie consommee en joule: "+ timeSecond*(moyenne/1000));

            GlobalBufferWriter.bw.write("Moyenne de la puissance en watt : "+moyenne/1000+"  ");;
            GlobalBufferWriter.bw.write("Temps en seconde : "+ timeSecond+"  ");;
            GlobalBufferWriter.bw.write("Energie consommee en joule: "+ timeSecond*(moyenne/1000)+"  ");;
            GlobalBufferWriter.bw.close();
            GlobalBufferWriter.fw.close();




        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}