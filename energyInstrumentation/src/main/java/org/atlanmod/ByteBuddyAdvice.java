package org.atlanmod;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class ByteBuddyAdvice {
    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<?> callable) throws Exception {
        System.out.println("Executing: "+method.getName());
        long start = System.nanoTime();
        try {
            return callable.call();
        } finally {
            System.out.println(method + " took " + (System.nanoTime() - start)+" nanoseconds");
        }
    }

    @Advice.OnMethodEnter
    static long onEnter() {
        return System.nanoTime();
    }

    @Advice.OnMethodExit
    static void exit(@Advice.Return long value) {
        System.out.println(System.nanoTime() - value);
    }

}