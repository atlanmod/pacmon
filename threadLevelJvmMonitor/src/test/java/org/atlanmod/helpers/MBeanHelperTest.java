package org.atlanmod.helpers;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.atlanmod.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.ThreadMXBean;

public class MBeanHelperTest {

    @Test
    public void connectToMXBean() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        int pid = (int) SystemUtils.getPID();
        MBeanServerConnection mBeanServerConnection = MBeanHelper.connectToLocalVM(pid);
        Assert.assertNotNull(mBeanServerConnection);
    }

    @Test
    public void getVirtualMachine() throws IOException, AttachNotSupportedException {
        int pid = (int) SystemUtils.getPID();
        VirtualMachine virtualMachine = MBeanHelper.getVirtualMachine(pid);
        Assert.assertNotNull(virtualMachine);
    }

    @Test
    public void getThreadMxBean() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        int pid = (int) SystemUtils.getPID();
        MBeanServerConnection mBeanServerConnection = MBeanHelper.connectToLocalVM(pid);
        ThreadMXBean threadMXBean = MBeanHelper.getThreadMXBean(mBeanServerConnection);

        Assert.assertNotNull(threadMXBean);
    }
}
