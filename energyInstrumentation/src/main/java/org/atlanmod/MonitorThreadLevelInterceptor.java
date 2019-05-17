package org.atlanmod;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

/**
 * Monitor to start the server
 */
class MonitorThreadLevelInterceptor {

    //called at the beginning of the method
    @Advice.OnMethodEnter
    static void enter() throws UnirestException {

        String tid = String.valueOf(Thread.currentThread().getId());

        Unirest.post("http://localhost:7070/startthreadlevel?pid="+SystemUtils.getPID()+"&tid="+tid)
                .header("accept", "application/json")
                .asJson()
                .getStatus();

        Unirest.post("http://localhost:7070/begintime?methodName="+Thread.currentThread().getStackTrace()[1].getMethodName()+"&timestamp="+System.currentTimeMillis())
                .header("accept", "application/json")
                .asString();

    }

    //called at the end of the method
    @Advice.OnMethodExit
    static void exit() throws UnirestException {

        Unirest.post("http://localhost:7070/endtime?methodName="+Thread.currentThread().getStackTrace()[1].getMethodName()+"&timestamp="+System.currentTimeMillis())
                .header("accept", "application/json")
                .asString();

        Unirest.post("http://localhost:7070/stopthreadlevel")
                .header("accept", "application/json")
                .asJson()
                .getStatus();

    }
}