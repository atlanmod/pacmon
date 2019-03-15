package org.atlanmod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class GlobalBufferWriter {
    public static FileWriter fw;
    public static BufferedWriter bw;

    GlobalBufferWriter(String pathFile) {
        try {
            File file = new File(pathFile);
            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new FileWriter(pathFile);
            bw = new BufferedWriter(fw);

            //bw.write("test scientifique");
        } catch (Exception e) {
            System.out.println("Fail to create the FileWriter for the file : " + pathFile);
        }
    }
    /*
    writeInFile(float mesure){}
        finally{
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            }
            catch(Exception e){
                System.out.println("Fail to close the FileWriter for the file : " + pathFile);

            }
        }
    }
    */
    public static void main( String[] args){
        System.out.println("test de la science universelle");
        new GlobalBufferWriter("/home/louis/git/TER_1_03_2019/jPowerApi/energyInstrumentation/src/main/resources/outputTER333.txt");
        try {
            GlobalBufferWriter.bw.write("lol56lolol");
            GlobalBufferWriter.bw.close();
            GlobalBufferWriter.fw.close();
        }
        catch (Exception e) {
            System.out.println("lol ça beug");
        }
    }
}