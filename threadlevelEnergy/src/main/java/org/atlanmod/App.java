package org.atlanmod;

import com.jvmtop.JvmTop;
import com.jvmtop.view.VMDetailView;

public class App {
    public App() {
    }

    public static void main(String[] args) throws Exception {
        (new Thread(() -> {
            for(long i = 0L; i < 9999999999L; ++i) {
                ++i;
            }

            System.out.println("done");
        })).start();
        JvmTop jvmTop = new JvmTop();
        jvmTop.setDelay(2.0D);
        jvmTop.run(new VMDetailView(21793, Integer.valueOf(1)));
    }
}
