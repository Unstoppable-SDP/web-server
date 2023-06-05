import java.io.*;
import java.util.concurrent.Semaphore;
 /**
 * @(#)LogFile.java
 *
 * @author 
 * by Hind Alrashid
 * @version 1.00 2023/6/4 
 * The LogFile class is a simple class that provides 
 * a method for writing messages to a log file named "webserver-log". 
 * The logFileOutput method takes a message parameter and uses 
 * a semaphore to ensure that only one thread can access 
 * the log file at a time.
 */

public class LogFile {
    private static final Semaphore semaphore = new Semaphore(1);

    public static void redirectConsoleToFile(String logFileName) throws Exception {
        
        File log = new File(logFileName);

        if(!log.exists()) {
            log.createNewFile();
        }
        semaphore.acquire();
        PrintStream logPrintStream = new PrintStream(new FileOutputStream(log, true));
        System.setOut(logPrintStream);
        semaphore.release();
    }

    public static void log(String message) {
        try {
            semaphore.acquire();
            System.out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }
}