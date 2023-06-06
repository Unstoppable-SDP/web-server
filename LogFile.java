import java.io.*;

/**
 * @(#)LogFile.java
 *
 * @author 
 * by Hind Alrashid
 * @version 1.00 2023/6/4 
 * The LogFile class contains a static method called 
 * redirectConsoleToFile that can be used to redirect console output 
 * to a log file. The method takes a file name as input and creates a new file
 * if it does not already exist. It then creates a PrintStream object with a
 * FileOutputStream object that writes to the log file. Finally, it sets the standard 
 * output stream to the logPrintStream object so that all console output is redirected 
 * to the specified log file. 
 */

public class LogFile {

    public static void redirectConsoleToFile(String logFileName) throws Exception {
        
        File log = new File(logFileName);

        if(!log.exists()) {
            log.createNewFile();
        }
        PrintStream logPrintStream = new PrintStream(new FileOutputStream(log, true));
        System.setOut(logPrintStream);
    }

}