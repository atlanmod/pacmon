package org.atlanmod;

import java.io.BufferedReader;
import java.io.FileReader;

public class EnergyCalculator {

    public EnergyCalculator() {

    }

    public void run(String filePath) {
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            double energy = 0.0;
            String s1 = bufferedReader.readLine();
            String s2 = bufferedReader.readLine();
            while (s2 != null) {
                long time = getTime(s2) - getTime(s1);
                energy += powerToEnergy(getPower(s2),time);
                s1 = s2;
                s2 = bufferedReader.readLine();
            }
            System.out.println("total energy:" + energy/1000 + "J");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getTime(String s) {
        return Long.parseLong(s.split(":")[0]);
    }

    private double getPower(String s) {
        return Double.parseDouble((s.split(":")[1]));
    }

    /**
     * Calculates energy (Joule) with the given power (Watt)
     * @param power Power in milliwatts as a double
     * @param time Time in milliseconds as a long
     * @return Energy in milliJoules as a double
     */
    public static double powerToEnergy(double power, long time) {
        return power*time/1000;
    }

}
