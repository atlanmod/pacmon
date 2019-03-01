package org.atlanmod;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws Exception
    {

        Thread.sleep(10000);

        long k = 0;

        for (long i = 0; i < 50000000000L; ++i) {
            k++;
        }

        Thread.sleep(5000);
    }
}