
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// the current implementation is used monitor we should use semaphore instead
public class ThreadPool {

	static final List<PoolThread> unloader = new LinkedList<PoolThread>();


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
				synchronized (pool) {
					pool.add(this);
					pool.notify();
				}
			}
		}

		synchronized void setR(Runnable r) {
			this.r = r;
			notify();
		}
	}

	private static int poolSize = -1;
	private static final Queue<PoolThread> pool = new LinkedList<PoolThread>();
	private static boolean singleThreaded = false;


	public void setPoolSize(int poolSize) throws ThreadPoolException {
		synchronized (pool) {
			if (ThreadPool.poolSize == -1) {
				if (poolSize < 1)
					throw new ThreadPoolException("pool size must be positive");
				ThreadPool.poolSize = poolSize;
				init();
			} else
				throw new ThreadPoolException("pool already initialized");
		}
	}

	public int getPoolSize() {
		synchronized (pool) {
			return poolSize;
		}
	}

	public void enqueue(Runnable r) {
		if (singleThreaded)
			r.run();
		else {
			PoolThread t = null;
			synchronized (pool) {
				if (poolSize == -1) {
					poolSize = Runtime.getRuntime().availableProcessors();
					init();
				}
				while (pool.isEmpty()) {
					try {
						pool.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				t = pool.remove();
			}
			t.setR(r);
		}
	}

	public void flush() {
		synchronized (pool) {
			while (pool.size() < poolSize) {
				try {
					pool.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void init() {
		for (int i = 0; i < poolSize; i++)
			pool.add(new PoolThread());
	}

	public void setSingleThreaded(boolean singleThreaded)
			throws ThreadPoolException {
		synchronized (pool) {
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