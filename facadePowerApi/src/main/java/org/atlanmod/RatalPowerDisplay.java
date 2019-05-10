package org.atlanmod;

import org.apache.commons.io.IOUtils;
import org.powerapi.PowerDisplay;
import org.powerapi.core.power.Power;
import org.powerapi.core.target.Target;
import scala.collection.immutable.Set;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class RatalPowerDisplay implements PowerDisplay {

    private File file;
    private FileOutputStream fileOutputStream;

    RatalPowerDisplay() {
        try {
            file = new File("./threadLevelJvmMonitor/src/main/resources/trace");
            if (!file.exists())
                file.mkdirs();
            File trace = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".txt", file);
            fileOutputStream = new FileOutputStream(trace);

            System.out.println("Trace writing in "+trace.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
