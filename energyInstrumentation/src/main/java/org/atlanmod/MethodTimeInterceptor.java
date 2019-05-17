package org.atlanmod;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

/**
 * Used to print the timestamps of the methods
 **/
public class MethodTimeInterceptor {

    //called at the beginning of the method
    @Advice.OnMethodEnter
    static void enter() throws UnirestException {
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
    }

}
