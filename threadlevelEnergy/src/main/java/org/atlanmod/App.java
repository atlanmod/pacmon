package org.atlanmod;


import com.jvmtop.JvmTop;
import com.jvmtop.view.VMDetailView;


public class App 
{
    public static void main( String[] args ) throws Exception {
        new Thread(() -> {
            for (long i = 0; i < 9999999999L; ++i) {
                i++;
            }
            System.out.println("done");
        }).start();

        JvmTop jvmTop = new JvmTop();
        jvmTop.setDelay(2d);

        //jvmTop.run(new VMOverviewView((int) SystemUtils.getPID())); //Overview of all VM running classes
        //jvmTop.run(new VMProfileView((int) SystemUtils.getPID(), 1)); //Overview of methods in running VM
        jvmTop.run(new VMDetailView((int) SystemUtils.getPID(), 1)); //Detail view of specific jvm

    }
}
