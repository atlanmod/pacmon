package org.atlanmod;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class HttpBasedMonitorInstrumenter {

    public static void premain(String args, Instrumentation instrumentation) {

        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform((builder, typeDescription, classLoader, javaModule) ->
                        builder.method(ElementMatchers.named("main")).intercept(Advice.to(HttpBasedMonitorAdvice.class)))
                .installOn(instrumentation);


    }
}
