import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;
 

public class LogFile {

	public static void main(String[] args) throws IOException, InterruptedException {
		//Our goal...
		
		for (int i = 0; i < 50; i++) {
			logFileOutput("I = " + i);
		}
	}
	
	public static void logFileOutput(String message) throws IOException, InterruptedException {
		Semaphore logSemaphore = new Semaphore(0);
		logSemaphore.acquire();
		File log = new File("webserver-log");
		
		if(!log.exists()) {
			log.createNewFile();
		}
		
		FileWriter fw = new FileWriter(log, true);
		BufferedWriter bw = new BufferedWriter(fw);
		
		bw.write(message);
		bw.newLine();

		bw.close();
		fw.close();
		logSemaphore.release();
		
	}

}