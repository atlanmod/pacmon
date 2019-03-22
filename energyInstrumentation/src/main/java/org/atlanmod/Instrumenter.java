package org.atlanmod;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import javax.xml.bind.Element;
import java.lang.instrument.Instrumentation;

public class Instrumenter {

    public static void premain(String args, Instrumentation instrumentation){

        new AgentBuilder.Default()
                .with(new AgentBuilder.InitializationStrategy.SelfInjection.Eager())
                .type(ElementMatchers.nameStartsWith("org.atlanmod"))
                .transform((builder, typeDescription, classLoader, javaModule) ->
                        builder .method(ElementMatchers.any()).intercept(Advice.to(MonitorInterceptor.class))
                                .method(ElementMatchers.named("main")).intercept(Advice.to(OnTheFlyAdvice.class))
                )
                .installOn(instrumentation);
    }

}

