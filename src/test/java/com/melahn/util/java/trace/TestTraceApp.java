package com.melahn.util.java.trace;

/**
 * This is an app that can be used to generate a simple, single-threaded trace file for the
 * purpose of testing the TraceVisualizer.
 * 
 * To generate a trace file follow these steps...
 * 
 *  cd <directory containing the class file generated from this source>
 *  export CLASSPATH=.
 *  jdb com.melahn.util.java.trace.TestTraceApp > example-trace-file.txt 
 *  ... enter the following commands (note that you can't see the outout from the commands
 *  ... because stdout is being redirected)
 *  ....... stop in com.melahn.util.java.trace.TestApp.main
 *  ....... run
 *  ....... exclude java.*, jdk.*
 *  ....... trace go methods
 *  ....... cont
 *  ... now observe that example-trace-file.txt contains the trace which can be used as inout to the
 *  ... TraceVisualizer.
 */

public class TestTraceApp {
    public static void main (String[] a) {
        A();
    }

    private static void A() {
        B();
        C();
        E();
        B();
    }

    private static void B() {
        /** Nothing to do */
    }
    
    private static void C() {
        D();
    }
    
    private static void D() {
        /** Nothing to do */
    }

    private static void E() {
        F();
    }
        
    private static void F() {
        G();
    }

    private static void G() {
        H();
    }
    
    private static void H() {
        I();
    }

    private static void I() {
        /** Nothing to do */
    }
    
}
