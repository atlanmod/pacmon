package org.atlanmod;

import io.mappedbus.MappedBusReader;
import io.mappedbus.MappedBusWriter;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MappedBusBasedMonitorTest {

    @Test
    public void checkWriteMonitorWithMethodname() throws IOException {
        File f = File.createTempFile("test1", ".map", new File("src/test/resources"));

        MappedBusWriter mappedBusWriter = new MappedBusWriter(f.getAbsolutePath(), 1000000L, 32);
        mappedBusWriter.open();
        mappedBusWriter.write(new MappedBusString("methodname", 32));

        MappedBusString methodExecution = new MappedBusString(32);
        MappedBusReader mappedBusReader = new MappedBusReader(f.getAbsolutePath(), 100000L, 32);
        mappedBusReader.open();
        mappedBusReader.readMessage(methodExecution);

        Assert.assertEquals("methodname", methodExecution.getString());
    }

    @Test
    public void checkWriteMonitorWithFloatValue() throws IOException {
        File f = File.createTempFile("test2", ".map", new File("src/test/resources"));

        MappedBusWriter mappedBusWriter = new MappedBusWriter(f.getAbsolutePath(), 1000000L, 32);
        mappedBusWriter.open();
        mappedBusWriter.write(new MappedBusFloat(25.5f));

        MappedBusFloat floatValue = new MappedBusFloat();
        MappedBusReader mappedBusReader = new MappedBusReader(f.getAbsolutePath(), 100000L, 32);
        mappedBusReader.open();
        mappedBusReader.readMessage(floatValue);

        Assert.assertEquals(25.5f, floatValue.getValue(), 0.0001);
    }

    @Test
    public void checkWriteMonitorSequential() throws IOException, InterruptedException {
        File f = new File("src/test/resources/test3");
        FileUtils.deleteDirectory(f);

        f.mkdirs();

        Files.walk(f.toPath()).forEach(internalFile -> internalFile.toFile().delete());

        long pid = SystemUtils.getPID();

        MappedBusBasedMonitor mappedBusBasedMonitor = new MappedBusBasedMonitor(f, (int) pid);
        mappedBusBasedMonitor.buildMonitorPowerApi();
        mappedBusBasedMonitor.run();

        Thread.sleep(500);

        File output = Files.walk(f.toPath()).filter(p -> p.toFile().getName().startsWith("power")).findFirst().get().toFile();

        MappedBusReader mappedBusReader = new MappedBusReader(output.getAbsolutePath(), 100000L, 32);
        mappedBusReader.open();

        MappedBusFloat mappedBusFloat = new MappedBusFloat();

        List<Float> floats = new ArrayList<>();

        while (mappedBusReader.next()) {
            mappedBusReader.readMessage(mappedBusFloat);
            floats.add(mappedBusFloat.getValue());
        }

        Assert.assertTrue(floats.size() > 0);
    }

    @Test
    public void checkWriteMonitorSequentialThread() throws IOException, InterruptedException {
        File f = new File("src/test/resources/test5");
        if (!f.exists())
            f.mkdirs();

        Files.walk(f.toPath()).forEach(internalFile -> internalFile.toFile().delete());

        MappedBusBasedMonitor mappedBusBasedMonitor = new MappedBusBasedMonitor(f, (int) Thread.currentThread().getId());
        mappedBusBasedMonitor.buildMonitorThreadLevel((int) Thread.currentThread().getId());
        mappedBusBasedMonitor.run();

        for (int i = 0; i < 100000; ++i) {
            i++;
            i--;
        }

        File output = Files.walk(f.toPath()).filter(p -> p.toFile().getName().startsWith("power")).findFirst().get().toFile();

        MappedBusReader mappedBusReader = new MappedBusReader(output.getAbsolutePath(), 100000L, 32);
        mappedBusReader.open();

        MappedBusFloat mappedBusFloat = new MappedBusFloat();

        List<Float> floats = new ArrayList<>();

        while (mappedBusReader.next()) {
            mappedBusReader.readMessage(mappedBusFloat);
            floats.add(mappedBusFloat.getValue());
        }

        Assert.assertTrue(floats.size() > 0);
    }

    @Test
    @Ignore
    public void checkPerformances() throws IOException {
        File f = new File("src/test/resources/test4/output");
        if (f.exists())
            f.delete();

        f.createNewFile();

        MappedBusWriter mappedBusWriter = new MappedBusWriter(f.getAbsolutePath(), 4300000, 32);
        mappedBusWriter.open();
        MappedBusFloat mappedBusFloat = new MappedBusFloat();

        long before = System.currentTimeMillis();

        for (int i = 0; i < 100000; ++i) {
            mappedBusFloat.setValue(i);
            mappedBusWriter.write(mappedBusFloat);
        }

        System.out.println("Wrote 100k messages in "+(System.currentTimeMillis() - before)+" ms");

        MappedBusReader mappedBusReader = new MappedBusReader(f.getAbsolutePath(), 4300000, 32);
        mappedBusReader.open();

        before = System.currentTimeMillis();
        int i = 0;

        while (mappedBusReader.next()) {
            mappedBusReader.readMessage(mappedBusFloat);
            i++;
        }

        Assert.assertEquals(100000, i);
        System.out.println("Read 100k messages in "+(System.currentTimeMillis() - before)+" ms");
    }
}
