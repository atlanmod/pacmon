package org.atlanmod;

import com.jvmtop.monitor.VMInfo;
import com.jvmtop.openjdk.tools.LocalVirtualMachine;

public class App {
    public App() {
    }

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            Thread.currentThread().setName("MY BIG PHAT THREAD HAS ID "+Thread.currentThread().getId());
            System.out.println("Thread started: "+Thread.currentThread().getId());

            for(long i = 0L; i < 99999999999L; ++i) {
                ++i;
            }
            //sun managment
            System.out.println("done");
            System.exit(1);
        });

        t1.start();
        /*
        JvmTop jvmTop = new JvmTop();
        jvmTop.setDelay(2.0D);
        jvmTop.run(new VMDetailView((int) SystemUtils.getPID(), 1));
        */

        VMInfo vmInfo = VMInfo.processNewVM(LocalVirtualMachine.getLocalVirtualMachine((int) SystemUtils.getPID()),1);

        while (t1.isAlive()) {
            vmInfo.update();
            System.out.println(vmInfo.getCpuLoad());
            Thread.sleep(500);
        }

    }
}
