
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

// the current implementation uses monitor we should use semaphore instead
public class ThreadPool {

	final List<PoolSingleThread> unloader = new LinkedList<PoolSingleThread>(); // unloader is the threadpool
	private final Queue<Runnable> buffer = new LinkedList<Runnable>(); // the queue for the buffer 
	private int poolSize = -1; // variable for the poolSize
	private int bufferSize = -1; // variable for max buffer size (used for overload handeling) 
	private String overLoadMethod = ""; // stores the overload handeling method (default block)
	Semaphore poolSemaphore; // this semaphore is used to block the threads when the buffer is empty
	Semaphore bufferSemaphore; // this semaphore is used to block the threads when the buffer is full

	
	ThreadPool(int threadNumber, int bufferSize, String overLoadMethod) throws ThreadPoolException {
		setPoolSize(threadNumber);
		setBufferSize(bufferSize);
		setOverLoadMethod(overLoadMethod);
		init();
	}

	// this method intilizes the pool and store them in buffer 
	private void init() {
		// initialize the pool
		for(int i=0; i<poolSize; i++){
			PoolSingleThread poolThreadRunnable = new PoolSingleThread(buffer, poolSemaphore, bufferSemaphore);
			unloader.add(poolThreadRunnable);
		}
		// start the threads
		for(PoolSingleThread runnable : unloader){
			new Thread(runnable).start();
		}
	}

	//getters

	// get method for pool size
	public int getPoolSize() {
			return poolSize;
	}

	// get method for buffer size 
	public int getBufferSize() {
		return bufferSize;
	}

	// get method for overload method 
	public String getOverLoadMethod () {
		return overLoadMethod;
	}

	// setters
	// The following method is used to initialize the thread pool
	// method to set pool size
	public void setPoolSize(int poolSize) throws ThreadPoolException {
		if (this.poolSize == -1) {
			if (poolSize < 1)
				throw new ThreadPoolException("pool size must be positive");
			this.poolSize = poolSize;
			this.poolSemaphore = new Semaphore(0);
		} else
			throw new ThreadPoolException("pool already initialized");
	}
	
	// method to set buffer size 
	public void setBufferSize(int bufferSize) throws ThreadPoolException {
		if (bufferSize < 1)
			throw new ThreadPoolException("buffer sizemust be positive");
		this.bufferSize = bufferSize;
		// initialize the semaphore
		bufferSemaphore = new Semaphore(bufferSize);
	}

	// method to set overload handeling method 
	public void setOverLoadMethod(String overLoadMethod) throws ThreadPoolException {
		if (overLoadMethod == "BLCK" || overLoadMethod == "DRPT" || overLoadMethod == "DRPH")
			this.overLoadMethod = overLoadMethod;
		else
			throw new ThreadPoolException("incorrect overload method");
	}


	public void enqueue(Runnable r) {
		// add the task to the queue
		// the overload handling is done here
		// if the buffer is full then the thread will wait
		try {
			System.out.println("The semaphor count is " + bufferSemaphore.availablePermits());
			bufferSemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.buffer.add(r);
		poolSemaphore.release();
		System.out.println("task added to buffer");
	}
	
	public void destroy() {
		// interrupt all threads in the pool
		for (PoolSingleThread thread : unloader) {
			thread.doStop();
		}
		// remove all tasks from the buffer
		buffer.clear();
	
	}
}



class ThreadPoolException extends Exception {
	public ThreadPoolException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}