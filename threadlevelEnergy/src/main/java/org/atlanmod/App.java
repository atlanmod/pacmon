package org.atlanmod;

import com.jvmtop.JvmTop;
import com.jvmtop.monitor.VMInfo;
import com.jvmtop.openjdk.tools.LocalVirtualMachine;
import com.jvmtop.view.ConsoleView;
import com.jvmtop.view.VMDetailView;

public class App {
    public App() {
    }

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            Thread.currentThread().setName("MY BIG PHAT THREAD ");
            System.out.println("Thread started: "+Thread.currentThread().getId());

            for(long i = 0L; i < 99999999999L; ++i) {
                ++i;
            }
            //sun managment
            System.out.println("done");
            System.exit(1);
        });

        t1.start();


        ThreadMonitor threadMonitor = new ThreadMonitor((int) SystemUtils.getPID(), 10);

        while (t1.isAlive()) {
            System.out.println(threadMonitor.getThreadCPUUsage()*100+"%");
            Thread.sleep(200);
        }

    }
}
