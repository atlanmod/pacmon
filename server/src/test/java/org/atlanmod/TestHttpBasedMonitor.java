package org.atlanmod;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestHttpBasedMonitor {

    @Test
    public void checkMainMethod() throws UnirestException, InterruptedException, IOException {
        HttpBasedMonitor.main(new String[]{});

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
    }
}
