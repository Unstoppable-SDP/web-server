
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

 /**
 * @(#)ThreadPool.java
 *
 * @authors
 * by Hind Alrashid, Marwah Bakoor, and Dinah Alshibi
 * @version 1.00 2023/6/4
 * The ThreadPool class represents a thread pool that manages a 
 * fixed number of threads to handle multiple client requests 
 * in a server application. It uses a buffer to store incoming 
 * requests and a semaphore to manage thread concurrency.
 */


public class ThreadPool {
	// The thread pool Variables
	final List<PoolSingleThread> unloader = new LinkedList<PoolSingleThread>(); // unloader is the threadpool
	private final Queue<RequestInfo> buffer = new LinkedList<RequestInfo>(); // the queue for the buffer 
	private int poolSize = -1; // variable for the poolSize
	private int bufferSize = -1; // variable for max buffer size (used for overload handeling) 
	private String overLoadMethod = ""; // stores the overload handeling method (default block)
	Semaphore poolSemaphore; // this semaphore is used to block the threads when the buffer is empty
	Semaphore bufferSemaphore; // this semaphore is used to block the threads when the buffer is full
	Semaphore mutex; // this semaphore is used to make sure that only one thread is accessing the buffer at a time
	ServeWebRequest sever; // socket for the connection
	monitor mon;

	// constructor
	ThreadPool(int threadNumber, int bufferSize, String overLoadMethod, ServeWebRequest s) throws Exception {
		setPoolSize(threadNumber);
		setBufferSize(bufferSize);
		setOverLoadMethod(overLoadMethod);
		this.sever = s;

		// initialize the semaphores
		this.poolSemaphore = new Semaphore(0);
		this.bufferSemaphore = new Semaphore(bufferSize);
		mutex = new Semaphore(1);
		init();
	}

	// Methods

	/**
	 * This method should be called at the beginning of the program
	 * It initializes the thread pool and starts the threads
	 */
	private void init() {
		// initialize the pool
		for(int i=0; i<poolSize; i++){
			PoolSingleThread poolThreadRunnable = new PoolSingleThread(buffer, poolSemaphore, bufferSemaphore,mutex, sever);
			unloader.add(poolThreadRunnable);
		}
		// start the threads
		for(PoolSingleThread runnable : unloader){
			new Thread(runnable).start();
		}
		// start the monitor
		mon = new monitor(unloader, buffer, poolSemaphore, bufferSemaphore, poolSize, mutex, sever);
		mon.start();
	}

	// Getters Methods

	/**
	 * This method returns the pool size
	 * @return poolSize
	 */
	public int getPoolSize() {
			return poolSize;
	}

	/**
	 * This method returns the buffer size
	 * @return bufferSize
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * This method returns the overload method
	 * @return overLoadMethod
	 */
	public String getOverLoadMethod () {
		return overLoadMethod;
	}

	// Setters Methods


	/**
	 * This method sets the pool size
	 * @param poolSize
	 * @throws Exception
	 */
	public void setPoolSize(int poolSize) throws  Exception {
		if (this.poolSize == -1) {
			if (poolSize < 1)
				throw new Exception("pool size must be positive");
			this.poolSize = poolSize;
		} else
			throw new Exception("pool already initialized");
	}

	/**
	 * This method sets the buffer size
	 * @param bufferSize
	 * @throws Exception
	 */

	public void setBufferSize(int bufferSize) throws Exception {
		if (bufferSize < 1)
			throw new Exception("buffer size must be positive");
		this.bufferSize = bufferSize;
	}

	/**
	 * This method sets the overload method
	 * @param bufferSize
	 * @throws Exception
	 */
	public void setOverLoadMethod(String overLoadMethod) throws Exception {
		if (overLoadMethod.equals("BLCK")|| overLoadMethod.equals("DRPT")|| overLoadMethod.equals("DRPH"))
			this.overLoadMethod = overLoadMethod;
		else
			throw new Exception("incorrect overload method");
	}

	/**
	 * This method adds a request to the buffer
	 * it will block, drop, or drop head based on the overload method
	 * if the buffer is full and the overload method is block, the thread will block
	 * if the buffer is full and the overload method is drop, the thread will drop the request
	 * if the buffer is full and the overload method is drop head, the thread will drop the first request in the buffer
	 * @param info the request to be added to the buffer
	 * @throws Exception
	 */
	public void enqueue(RequestInfo info) {
		try {
		
		if(bufferSemaphore.availablePermits() == 0){
			if(overLoadMethod.equals("DRPT")){
				//System.out.println("task dropped");
				sever.refuse(info.getSocket(), info.getQueueCount());
				info.getSocket().close();
				System.out.println("problem serving request "+info.getSocket());
				//info.getSocket().getInetAddress();
				return;
			} else if(overLoadMethod.equals("DRPH")){
				mutex.acquire();
				RequestInfo firstReqInfo =buffer.poll();
				bufferSemaphore.release();
				mutex.release();
				sever.refuse(firstReqInfo.getSocket(), firstReqInfo.getQueueCount());
				System.out.println("problem serving request "+firstReqInfo.getSocket());
				firstReqInfo.getSocket().close();
			}
		}
			bufferSemaphore.acquire();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		System.out.println("Connecton "+info.getQueueCount()+" queued. ("+new Date()+")");
		this.buffer.add(info);
		poolSemaphore.release();
		//System.out.println("task added to buffer");
		
	}

	/**
	 * This method closes the thread pool
	 * it will interrupt all threads in the pool and remove all tasks from the buffer
	 * @throws Exception
	 */

	public void destroy() throws Exception {
		//System.out.println("Thread close: ");
		System.out.println(new Date()+" The server is shutting down ...");
		try {
			mon.doStop();
			// interrupt all threads in the pool
			for (PoolSingleThread thread : unloader) {
				thread.doStop();
			}
			// remove all tasks from the buffer
			int count=0;
			while(buffer.size()!=0){
				mutex.acquire();
				RequestInfo reqInfo =buffer.poll();
				mutex.release();
				sever.refuse(reqInfo.getSocket(), reqInfo.getQueueCount());
				reqInfo.getSocket().close();
				count++;
				//System.out.println("Thread close: "+ reqInfo.getQueueCount());
			}
			System.out.println(new Date()+" Flushing "+count+" buffers ...");
			System.out.println(new Date()+" The thread pool is shutting down ...");

		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}
