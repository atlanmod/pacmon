package org.atlanmod;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import javax.xml.bind.Element;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.security.ProtectionDomain;

public class Instrumenter {

    public static void premain(String args, Instrumentation instrumentation){
        new AgentBuilder.Default()
                .with(new AgentBuilder.InitializationStrategy.SelfInjection.Eager())
                .type(ElementMatchers.nameStartsWith(args))
                .transform((builder, typeDescription, classLoader, javaModule) ->
                        builder .method(ElementMatchers.any()).intercept(Advice.to(MethodTimeInterceptor.class))
                )
                .transform((builder, typeDescription, classLoader, javaModule) ->
                        builder //.method(ElementMatchers.named("main")).intercept(Advice.to(MonitorPowerApiInterceptor.class))
                                .method(ElementMatchers.named("main")).intercept(Advice.to(MonitorThreadLevelInterceptor.class))
                )
                .installOn(instrumentation);
    }

}

