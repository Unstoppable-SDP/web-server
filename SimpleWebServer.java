import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * @(#)SimpleWebServer.java
 *
 * @author Adapted from SSaurel's Blog
 * by Dr. Abdulghani M. Al-Qasimi
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
	static int threadNumber = 2;

	// buffer size

	static int bufferSize = 1;

	// overload method
	static String overload = "DRPT";

	// Client Connection via Socket Class
	static Socket connect;

	// connection ID
	static int count = 0;

	private static ThreadPool pool;

	// convert the following method to multi-threaded
	// to handle multiple clients simultaneously
	// and to improve the performance of the server
	// by using a thread pool
	//

	public static void main(String[] args) throws IOException {
	
		try {

			// get the port number, thread number buffer size and overload method
			if (args.length > 0) {
				PORT = Integer.parseInt(args[0]);
				threadNumber = Integer.parseInt(args[1]);
				bufferSize = Integer.parseInt(args[2]);
				overload = args[3];
			} 

			pool= new ThreadPool(threadNumber, bufferSize, overload);

			// create a server listening socket
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
			//write into webserver-log
			LogFile.logFileOutput("Server started.\nListening for connections on port : " + PORT + " ...\n");
			// create one instance of the required task
			// ServeWebRequest s = new ServeWebRequest();

			// listen until user halts server execution

			try {
				while (true) {
					// accept client connection request
					connect = serverConnect.accept(); 
					count++;
					System.out.println("[SERVER] Connected to client!");
					//write into webserver-log
					LogFile.logFileOutput("[SERVER] Connected to client!");
					ClientHandler clientThread = new ClientHandler(connect,count);
					pool.enqueue(clientThread);
					// close the connection
				}
			} finally {
				// pool.destroy();
				serverConnect.close();
			}

		} catch (Exception e) {
			System.err.println("Server Connection error : " + e.getMessage());
			LogFile.logFileOutput("Server Connection error : " + e.getMessage());
		}
	}
}

