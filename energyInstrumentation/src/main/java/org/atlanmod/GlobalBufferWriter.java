package org.atlanmod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public final class GlobalBufferWriter {
    private static volatile GlobalBufferWriter globalBufferWriter = null;
    public static FileWriter fw;
    public static BufferedWriter bw;

    private GlobalBufferWriter(String pathFile) {
        super();
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

    public final static GlobalBufferWriter getInstance(String pathFile) {
        //Le "Double-Checked Singleton"/"Singleton doublement vérifié" permet
        //d'éviter un appel coûteux à synchronized,
        //une fois que l'instanciation est faite.
        if (GlobalBufferWriter.globalBufferWriter == null) {
            // Le mot-clé synchronized sur ce bloc empêche toute instanciation
            // multiple même par différents "threads".
            // Il est TRES important.
            synchronized(GlobalBufferWriter.class) {
                if (GlobalBufferWriter.globalBufferWriter == null) {
                    GlobalBufferWriter.globalBufferWriter = new GlobalBufferWriter(pathFile);
                }
            }
        }
        return GlobalBufferWriter.globalBufferWriter;
    }

    public void writeInfile(float s){
        try {
            GlobalBufferWriter.getInstance("").bw.write(s + "\n");
        }
        catch(Exception e){

        }
    }

    public static void main( String[] args) {
        System.out.println("test de la science universelle");
        GlobalBufferWriter gb = GlobalBufferWriter.getInstance("/home/louis/git/TER_1_03_2019/jPowerApi/energyInstrumentation/src/main/resources/outputTER333.txt");
        try {
            gb.bw.write("lol56lolol");
            gb.bw.close();
            gb.fw.close();
        }
        catch (Exception e) {
            System.out.println("lol ça beug");
        }
    }
}