import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


/**
 * @(#)SimpleWebServer.java
 *
 * @author Adapted from SSaurel's Blog
 * by Dr. Abdulghani M. Al-Qasimi
 * @Editor Marwah Bakoor, Hind Alrashid, Dinah Alshibi
 * @version 1.00 2020/8/7
 *
 * This is a simple web server for teaching purposes.
 * It works as a single threaded application, where,
 * while a client request is being served, other clients
 * will have to wait until that request is finished.
 */
public class SimpleWebServer {

	// verbose mode
	static final boolean verbose = true;

	// port to listen for connection
	static int PORT = 8085;

	// thread number
	static int threadNumber = 7;

	// buffer size

	static int bufferSize = 10;

	// overload method
	static String overload = "BLCK";

	// Client Connection via Socket Class
	static Socket connect;

	// connection ID
	static int count = 0;

	private static ThreadPool pool;

	/**
	 * Main method
	 * This method is the program entry point
	 * It creates a server socket to listen for client connection requests
	 * It creates a thread pool to handle the requests
	 * It creates a task to handle the requests
	 * @param args the command line arguments
	 */

	public static void main(String[] args) throws Exception {
	    //System.out.println(PORT+" "+threadNumber+" "+bufferSize+" "+overload);
		try {
			if (args.length >1) {
				PORT = Integer.parseInt(args[0]);
			}if (args.length >2){
				threadNumber = Integer.parseInt(args[1]);
			}if (args.length >3){
				bufferSize = Integer.parseInt(args[2]);
			}if (args.length >4){
				overload = args[3];
			}
			//System.out.println(PORT+" "+threadNumber+" "+bufferSize+" "+overload);
			// create a server listening socket
			ServerSocket serverConnect = new ServerSocket(PORT);

			// log file
			LogFile.redirectConsoleToFile("my-log-file.txt");
			
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
			//write into webserver-log
			// create one instance of the required task
			ServeWebRequest s = new ServeWebRequest();

			pool= new ThreadPool(threadNumber, bufferSize, overload,s);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("Shutting down...");
				try {
					pool.destroy();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}));
			// listen until user halts server execution

			try {
				while (true) {
					System.out.println("[SERVER] Waiting for client connection request...");
					// accept client connection request
					connect = serverConnect.accept(); 
					count++;
					System.out.println("[SERVER] Connected to client!");
					//write into webserver-log

					// create a new thread to handle the request
					pool.enqueue(new RequestInfo(connect, count));
					// close the connection 
					System.out.println("Connecton "+count+" opened. ("+new Date()+")");
				}
			} finally {
				serverConnect.close();
			}

		} catch (Exception e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}
}

