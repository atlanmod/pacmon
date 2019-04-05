package org.atlanmod;

import com.jvmtop.JvmTop;
import com.jvmtop.view.VMDetailView;

public class App {
    public App() {
    }

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            Thread.currentThread().setName("MY BIG PHAT THREAD");
            System.out.println("Thread started: "+Thread.currentThread().getId());

            for(long i = 0L; i < 99999999999L; ++i) {
                ++i;
            }

            System.out.println("done");
            System.exit(1);
        });

        t1.start();
        JvmTop jvmTop = new JvmTop();
        jvmTop.setDelay(2.0D);
        jvmTop.run(new VMDetailView((int) SystemUtils.getPID(), 1));
    }
}
