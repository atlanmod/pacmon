package org.atlanmod;

import com.jvmtop.monitor.VMInfo;
import com.jvmtop.openjdk.tools.LocalVirtualMachine;
import com.jvmtop.openjdk.tools.ProxyClient;

import java.lang.management.ThreadMXBean;

public class ThreadMonitor {

    private int tid;

    private double oldCPUUptime;
    private double oldThreadCPUTime;
    private double CPUUptime;
    private double threadCPUTime;
    private double threadDelta;
    private double delta;

    private ThreadMXBean threadMXBean;
    private ProxyClient proxyClient;
    private VMInfo vmInfo;

    public ThreadMonitor(int VMPID, int tid) {
        try {
            this.tid = tid;
            vmInfo = VMInfo.processNewVM(LocalVirtualMachine.getLocalVirtualMachine(VMPID),1);
            proxyClient = vmInfo.getProxyClient();
            threadMXBean = vmInfo.getThreadMXBean();
            oldCPUUptime = proxyClient.getProcessCpuTime();
            threadCPUTime = threadMXBean.getThreadCpuTime(tid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getThreadCPUUsage() {
        try {
            update();
            return (threadDelta  / delta);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Updates system values
     * @throws Exception if {@link ProxyClient} cannot fetch CPU times
     */
    private void update() throws Exception {
        proxyClient.flush();

        //Getting new thread values
        oldThreadCPUTime = threadCPUTime;
        threadCPUTime = threadMXBean.getThreadCpuTime(tid);

        //Getting new CPU values
        oldCPUUptime = CPUUptime;
        CPUUptime = proxyClient.getProcessCpuTime();

        //Computing Delta since last update to get average running duration
        threadDelta = threadCPUTime - oldThreadCPUTime;
        delta = CPUUptime - oldCPUUptime;
    }

}

