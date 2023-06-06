import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;


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
	static int threadNumber = 8;

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
	    System.out.println(PORT+" "+threadNumber+" "+bufferSize+" "+overload);
		Scanner scanner=new Scanner(System.in);
		try {
			if (args.length >=1) {
				PORT = Integer.parseInt(args[0]);
			}if (args.length >=2){
				threadNumber = Integer.parseInt(args[1]);
			}if (args.length >=3){
				bufferSize = Integer.parseInt(args[2]);
			}if (args.length >=4){
				overload = args[3];
			}
			if (args.length ==0) {
				System.out.println("Enter the port number or press enter for deafult (Port=8085)");
				String por= scanner.nextLine().trim();
				if(!por.isEmpty()){
					try{
						PORT=Integer.parseInt(por);
					}catch(NumberFormatException e){
						System.out.println("Invalid port number, the default was set.");
					}
				}
				System.out.println("Enter the number of thread or press enter for deafult (Number of thread =7)");
				String thr= scanner.nextLine().trim();
				if(!thr.isEmpty()){
					try{
						threadNumber=Integer.parseInt(thr);
					}catch(NumberFormatException e){
						System.out.println("Invalid number of thread, the default was set.");
					}
				}
				System.out.println("Enter the buffer size or press enter for deafult (Buffer Size =10)");
				String buf= scanner.nextLine().trim();
				if(!buf.isEmpty()){
					try{
						bufferSize=Integer.parseInt(buf);
					}catch(NumberFormatException e){
						System.out.println("Invalid buffer size, the default was set.");
					}
				}
				System.out.println("Enter the overload method or press enter for deafult (overload = BLCK)");
				String over= scanner.nextLine().trim();
				if(!over.isEmpty()){
					if(over.equals("BLCK")|| over.equals("DRPT")|| over.equals("DRPH")){
						overload=over;
					}else {
						System.out.println("Invalid option, the default was set.");
					}
					
				}
			}
			System.out.println(PORT+" "+threadNumber+" "+bufferSize+" "+overload);
			// create a server listening socket
			ServerSocket serverConnect = new ServerSocket(PORT);
			
			// log file
			LogFile.redirectConsoleToFile("my-log-file.txt");
			//System.out.println(serverConnect.getLocalPort()+" "+ serverConnect.getInetAddress()+" "+serverConnect.getLocalSocketAddress()+" ");

			System.out.println(new Date()+": Server started.\nListening for connections on port : " + PORT + " ...\n");
			//write into webserver-log
			// create one instance of the required task
			ServeWebRequest s = new ServeWebRequest();

			pool= new ThreadPool(threadNumber, bufferSize, overload,s);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println(new Date()+" Received shutdown signal ...");
				try {
					pool.destroy();
					System.out.println(new Date()+" Done. Total was "+count+" threads.");
					//serverConnect.close();
					System.out.println(new Date()+" Server connection socket closed.");
					System.out.println(new Date()+" The server exits.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
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
					
				}
			} finally {
				serverConnect.close();
				if(scanner!=null)
				scanner.close();
			}

		} catch (Exception e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}
}

