
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// the current implementation is used monitor we should use semaphore instead
public class ThreadPool {

	static final List<PoolThread> unloader = new LinkedList<PoolThread>(); // unloader is the threadpool


	public void destroy() {
		for (PoolThread t : unloader)
			t.apoptosis();
	}

	private static class PoolThread extends Thread {
		private boolean done = false;
		PoolThread() {
			super();
			setDaemon(true);
			start();
			unloader.add(this);
		}

		synchronized void apoptosis() {
			done = true;
			interrupt();
		}

		private Runnable r;

		@Override
		public void run() {
			while (true) {
				synchronized (this) {
					while (!done && r == null) {
						try {
							wait();
						} catch (InterruptedException e) {
							if (!done)
								e.printStackTrace();
						}
					}
				}
				if (done)
					break;
				try {
					r.run();
				} catch (Throwable t) {
					t.printStackTrace();
				}
				r = null;
				synchronized (buffer) {
					buffer.add(this);
					buffer.notify();
				}
			}
		}

		synchronized void setR(Runnable r) {
			this.r = r;
			notify();
		}
	}

	private static int poolSize = -1; // variable for the poolSize
	private static int bufferSize = 10; // variable for max buffer size (used for overload handeling)
	private static String overLoadMethod = "BLCK"; // stores the overload handeling method (default block)
	private static final Queue<PoolThread> buffer = new LinkedList<PoolThread>(); // the queue for the buffer 
	private static boolean singleThreaded = false;

	
	public void setPoolSize(int poolSize) throws ThreadPoolException {
		if (ThreadPool.poolSize == -1) {
			if (poolSize < 1)
				throw new ThreadPoolException("pool size must be positive");
			ThreadPool.poolSize = poolSize;
			init();
		} else
			throw new ThreadPoolException("pool already initialized");
	}

	// method to set buffer size 
	public void setBufferSize(int bufferSize) throws ThreadPoolException {
		if (bufferSize < 1)
			throw new ThreadPoolException("buffer sizemust be positive");
		ThreadPool.bufferSize = bufferSize;
	}

	// method to set overload handeling method 
	public void setOverLoadMethod(String overLoadMethod) throws ThreadPoolException {
		if (overLoadMethod == "BLCK" || overLoadMethod == "DRPT" || overLoadMethod == "DRPH")
			ThreadPool.overLoadMethod = overLoadMethod;
		else
			throw new ThreadPoolException("incorrect overload method");
	}

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

	public void enqueue(Runnable r) {
		if (singleThreaded)
			r.run();
		else {
			PoolThread t = null;
			synchronized (buffer) {
				if (poolSize == -1) {
					poolSize = Runtime.getRuntime().availableProcessors();
					init();
				}
				while (buffer.isEmpty()) {
					try {
						buffer.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				t = buffer.remove();
			}
			t.setR(r);
			// if (buffer.size() == bufferSize) {
			// 	overLoadHandle(t, r);
			// }
		}
	}

	public void flush() {
		synchronized (buffer) {
			while (buffer.size() < bufferSize) {
				try {
					buffer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// this method intilizes the pool and store them in buffer 
	private void init() {
		int capacity = poolSize - bufferSize; // variable to check if pool size fits in buffer 
		int occupied = bufferSize; // to store how much space is left in the buffer 
	
		// intialize pool thread (unloader)
		for (int i = 0; i < poolSize; i++)
		{
			buffer.add(new PoolThread());
		}

		// store the threads in the buffer as allowed by buffer size 
		// for (int i = 0; i < bufferSize; i++)
		// {
		// 	if (i < poolSize)
		// 		buffer.add(unloader.get(i));
		// 	else // if buffer size is bigger than poolsize exit 
		// 		occupied = i; // the space occupied is less than buffer size 
		// 		break;
		// }

		// if (capacity > 0 && buffer.size() == bufferSize) // check if pool size is bigger than buffer size
		// 	overLoadHandle(capacity); // handle the overload 
	}

	// method for handeling the overload of the threads 
	public void overLoadHandle(int capacity) {
		int start = poolSize - capacity;
		switch (overLoadMethod) { 
			case "BLCK":
				for (int i = start; i < poolSize; i++) {
					if (buffer.size() == bufferSize)
						try {
							wait();
						} catch (InterruptedException e) {
						}
					else
						buffer.add(unloader.get(i));
				}
						
				break;

			case "DRPT":
				for (int i = start; i < poolSize; i++) {
					if (buffer.size() == bufferSize)
						unloader.get(i).apoptosis();
					else
						buffer.add(unloader.get(i));
				}

				break;
					
			case "DRPH":
				for (int i = start; i < poolSize; i++) {
					if (buffer.size() == bufferSize)
						unloader.get(unloader.indexOf(buffer.remove())).apoptosis();
					else
						buffer.add(unloader.get(i));
				}				

				break;
				
			default:
			for (int i = start; i < poolSize; i++) {
				if (buffer.size() == bufferSize)
					try {
						wait();
					} catch (InterruptedException e) {
					}
				else
					buffer.add(unloader.get(i));
			}
					
				break;

		}
	}


	public void setSingleThreaded(boolean singleThreaded)
			throws ThreadPoolException {
		synchronized (buffer) {
			if (poolSize > -1)
				throw new ThreadPoolException(
						"setting single threaded after thread pool initialized");
			ThreadPool.singleThreaded = singleThreaded;
			poolSize = 0;
		}
	}

	public boolean isSingleThreaded() {
		return singleThreaded;
	}
}


class ThreadPoolException extends Exception {
	public ThreadPoolException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}