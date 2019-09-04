package org.atlanmod;

import com.google.common.io.Files;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.util.log.Log;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import sun.net.www.http.HttpClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TestHttpBasedMonitor {

    @Test
    public void checkMainMethod() throws UnirestException, InterruptedException, IOException {
        new HttpBasedMonitor().run();

        int status = Unirest.post("http://localhost:7070/startpowerapi?pid="+SystemUtils.getPID())
                .header("accept", "application/json")
                .asJson()
                .getStatus();
        Assert.assertEquals(202, status);

        status = 0; // ensures that the status changes

        System.out.println("Sleeping");
        Thread.sleep(5000);
        System.out.println("Done sleeping");

        status= Unirest.post("http://localhost:7070/stoppowerapi")
                .header("accept", "application/json")
                .asJson()
                .getStatus();

        Assert.assertEquals(202, status);

        Unirest.post("http://localhost:7070/stoppowerapi")
                .header("accept", "application/json")
                .asJson()
                .getStatus();
    }

    @Test
    public void checkMainMethodWithFileParameter() throws IOException, UnirestException {
        File file = Files.createTempDir();
        new HttpBasedMonitor().run();

        int status = Unirest.post("http://localhost:7070/startpowerapi?pid="+SystemUtils.getPID()+"&uri="+file.getAbsolutePath())
                .header("accept", "application/json")
                .asJson()
                .getStatus();

        Assert.assertEquals(202, status);

        java.nio.file.Files.walk(file.toPath()).forEach(System.out::println);
        Assert.assertTrue(file.exists());
        Assert.assertTrue(java.nio.file.Files.walk(file.toPath()).count() > 0);

        Unirest.post("http://localhost:7070/stoppowerapi")
                .header("accept", "application/json")
                .asJson()
                .getStatus();
    }

    @Test
    public void checkServerStop() throws IOException, UnirestException {

        new HttpBasedMonitor().run();
        int status = Unirest.post("http://localhost:7070/startpowerapi?pid="+SystemUtils.getPID())
                .header("accept", "application/json")
                .asJson()
                .getStatus();
        Assert.assertEquals(202, status);

        //Checking that the server cannot be start because it is already running
        status = Unirest.post("http://localhost:7070/startpowerapi?pid="+SystemUtils.getPID())
                .header("accept", "application/json")
                .asJson()
                .getStatus();

        Assert.assertEquals(500, status);

        //Stopping it
        status = Unirest.post("http://localhost:7070/stoppowerapi")
                .header("accept", "application/json")
                .asJson()
                .getStatus();

        Assert.assertEquals(202, status);

        //Restarting it to check that the previous one has been stopped, and that *now* it can be restarted
        status = Unirest.post("http://localhost:7070/startpowerapi?pid="+SystemUtils.getPID())
                .header("accept", "application/json")
                .asJson()
                .getStatus();

        Assert.assertEquals(202, status);

        //Stopping it for good
        status = Unirest.post("http://localhost:7070/stoppowerapi")
                .header("accept", "application/json")
                .asJson()
                .getStatus();

        Assert.assertEquals(202, status);
    }

    @Test
    public void checkMainMethodWithTimestampParameter() throws IOException, UnirestException, InterruptedException {
        File f = new File("trace"+System.currentTimeMillis());
        Assert.assertTrue(f.mkdir());

        new HttpBasedMonitor().run();

        int status = Unirest.post("http://localhost:7070/startpowerapi?pid="+SystemUtils.getPID()+"&uri="+f.getAbsolutePath())
                .header("accept", "application/json")
                .asJson()
                .getStatus();

        Assert.assertEquals(202, status);
        //Server successfully started;

        Unirest.post("http://localhost:7070/begintime?name=checkMainMethodWithTimestampParameter&timestamp="+System.nanoTime())
                .header("accept", "application/json")
                .asJson()
                .getStatus();
        
        Thread.sleep(500);

        Unirest.post("http://localhost:7070/endtime?name=checkMainMethodWithTimestampParameter&timestamp="+System.nanoTime());

        status = Unirest.post("http://localhost:7070/stoppowerapi")
                .header("accept", "application/json")
                .asJson()
                .getStatus();

        Assert.assertEquals(202, status);

        Thread.sleep(2000);
        FileUtils.deleteDirectory(f);
    }

    @After
    public void tearDown() throws IOException {
        new ProcessBuilder().command("kill $(lsof -t -i:7070)").start();
    }
}
