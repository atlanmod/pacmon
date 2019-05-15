package org.atlanmod;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

/**
 * Monitor to start the server
 */
class MonitorPowerApiInterceptor {

    //called at the beginning of the method
    @Advice.OnMethodEnter
    static void enter(@Advice.Origin Method method, @Advice.Local("duration") Long duration) throws UnirestException {

        Unirest.post("http://localhost:7070/startpowerapi?pid="+SystemUtils.getPID())
                .header("accept", "application/json")
                .asJson()
                .getStatus();
        Unirest.post("http://localhost:7070/begintime?methodName="+Thread.currentThread().getStackTrace()[1].getMethodName()+"&timestamp="+System.currentTimeMillis())
                .header("accept", "application/json")
                .asString();

    }

    //called at the end of the method
    @Advice.OnMethodExit
    static void exit(@Advice.Origin Method method, @Advice.Local("duration") Long duration) throws UnirestException {

        Unirest.post("http://localhost:7070/endtime?methodName="+Thread.currentThread().getStackTrace()[1].getMethodName()+"&timestamp="+System.currentTimeMillis())
                .header("accept", "application/json")
                .asString();
        Unirest.post("http://localhost:7070/stoppowerapi")
                .header("accept", "application/json")
                .asJson()
                .getStatus();

    }
}
