package org.atlanmod.metrics;

import org.atlanmod.SystemUtils;
import com.mashape.unirest.http.Unirest;

public class ServerSleepLoop {

    private int pid;

    public ServerSleepLoop() {
        pid = (int) SystemUtils.getPID();
    }

    public void run() {
        Unirest.post("http://localhost:7070/start?pid="+SystemUtils.getPID())
                .header("accept", "application/json")
                .asJson()
                .getStatus();

        for (int i=0;i<10;i++){
            sleepMethod();
        }
    }

    public void sleepMethod(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("I'm awake");
        addMethod();
        //System.out.println("I finished adding");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("byebye");
    }

    public void addMethod(){
        for(long i = 0L; i < 999999999L; ++i) {
            ++i;
        }
    }

}
