package org.atlanmod;

import org.apache.commons.io.IOUtils;
import org.powerapi.PowerDisplay;
import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import scala.collection.immutable.Set;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class PacmanPowerDisplay implements PowerDisplay {

    private File file;
    private FileOutputStream fileOutputStream;
    private String fileOutputPath;

    public PacmanPowerDisplay() {
        try {
            file = new File("threadLevelJvmMonitor/src/main/resources/trace");
            if (!file.exists())
                file.mkdirs();
            File trace = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".txt", file);
            fileOutputStream = new FileOutputStream(trace);

            this.fileOutputPath = trace.getAbsolutePath();
            System.out.println("Trace writing in "+fileOutputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFileOutputPath() {
        return this.fileOutputPath;
    }

    @Override
    public void display(UUID muid, long timestamp, Set<Target> targets, Set<String> devices, Power power) {
        try {
            IOUtils.write(String.valueOf(timestamp) + ":" + String.valueOf(power.toMilliWatts())+"\n", fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
