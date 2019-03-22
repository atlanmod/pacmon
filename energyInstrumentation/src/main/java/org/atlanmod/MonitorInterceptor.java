package org.atlanmod;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

class MonitorInterceptor {


    //called at the beginning of the method
    @Advice.OnMethodEnter
    static void enter(@Advice.Origin Method method, @Advice.Local("duration") Long duration) {
        duration = System.nanoTime();
        System.out.println("Im in "+method.toGenericString());
    }

    //called at the end of the method
    @Advice.OnMethodExit
    static void exit(@Advice.Origin Method method, @Advice.Local("duration") Long duration) {
        System.out.println("Im out "+method.toGenericString()+" lasted "+(System.nanoTime() - duration));
    }
}
