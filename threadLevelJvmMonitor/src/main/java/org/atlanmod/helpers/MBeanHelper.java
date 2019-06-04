package org.atlanmod.helpers;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import sun.management.ConnectorAddressLink;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Probably does not work for Java 9+
 */
public class MBeanHelper {

    /**
     * Return the {@link MBeanServerConnection} from a local VM
     * @param pid the PID of the VM
     * @return the corresponding @{@link MBeanServerConnection}
     * @throws IOException if PID does not exist
     * @throws AttachNotSupportedException if attachment not supported
     * @throws AgentLoadException if given jar is not suitable for injection
     * @throws AgentInitializationException if given jar cannot be properly loaded
     */
    public static MBeanServerConnection connectToLocalVM(int pid) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        String address = ConnectorAddressLink.importFrom(pid);

        if (address == null) {
            enableManagementInVM(pid);
            address = ConnectorAddressLink.importFrom(pid);
        }

        JMXServiceURL jmxUrl = new JMXServiceURL(address);

        return JMXConnectorFactory.connect(jmxUrl).getMBeanServerConnection();
    }

    /**
     * Enable Management in the JVM wth given PID
     * @param pid the PID of the Jvm
     * @throws IOException if PID does not exist
     * @throws AttachNotSupportedException if attachment not supported
     * @throws AgentLoadException if given jar is not suitable for injection
     * @throws AgentInitializationException if given jar cannot be properly loaded
     */
    private static void enableManagementInVM(int pid) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        VirtualMachine virtualMachine = getVirtualMachine(pid);
        File file = getManagementJar();
        virtualMachine.loadAgent(file.getCanonicalPath());
    }

    /**
     * Return a local Virtual Machine with the associated pid
     * @param pid the pid of a jvm
     * @return a {@link VirtualMachine}
     * @throws IOException if PID cannot be found
     * @throws AttachNotSupportedException if VM does not enable attachment
     */
    public static VirtualMachine getVirtualMachine(int pid) throws IOException, AttachNotSupportedException {
        return VirtualMachine.attach(String.valueOf(pid));
    }

    /**
     * https://stackoverflow.com/a/26411383/7158736
     */
    public static File getManagementJar() {
        String home = System.getProperty("java.home");
        String agent = home + File.separator + "jre" + File.separator + "lib"
                + File.separator + "management-agent.jar";
        File f = new File(agent);
        if (!f.exists()) {
            agent = home + File.separator + "lib" + File.separator +
                    "management-agent.jar";
            f = new File(agent);
            if (!f.exists()) {
                throw new RuntimeException("management-agent.jar missing");
            }
        }
        return f;
    }

    public static ThreadMXBean getThreadMXBean(MBeanServerConnection mBeanServerConnection ) throws IOException {
        return ManagementFactory.getPlatformMXBean(mBeanServerConnection, ThreadMXBean.class);
    }
}
