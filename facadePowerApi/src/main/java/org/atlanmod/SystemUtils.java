package org.atlanmod;

public class SystemUtils {

    private SystemUtils() {}

    public static long getPID() {

        String processName =
                java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }

}
