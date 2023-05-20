import java.net.Socket;
import java.util.concurrent.*;

//class ThreadPoolExample {

    // public static void main(String[] args) {
    //     // Create a thread pool with 5 threads
    //     ThreadPool pool = new ThreadPool(5,"h",7);

    //     // Submit tasks to the thread pool
    //     for (int i = 0; i < 10; i++) {
    //         pool.execute(new Task(i));
    //     }

    //     // Shutdown the thread pool
    //     pool.shutdown();
    // }

public  class ThreadPool {
    private int maxPool;
    private String oHandle;
    private int maxBuffer;
    private PoolWorker[] threads;
    //private ClientHandler serverRequest;
    private BlockingQueue<Runnable> queue;
    private Semaphore sem;

    public ThreadPool(int maxPool,String oHandle,int maxBuffer){
        //Initialize the variable
        this.maxPool=maxPool;
        this.maxBuffer=maxBuffer;
        this.oHandle=oHandle;
        sem = new Semaphore(maxBuffer);
        queue = new LinkedBlockingQueue<>();
        threads = new PoolWorker[maxPool];
        //Create threads
        for (int i = 0; i < maxPool; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }

    public void execute(Runnable task) {
        synchronized (queue) {
            queue.add(task);
            queue.notify();
        }
    }

    public void shutdown(){
        for (PoolWorker worker : threads) {
            worker.stopWorker();
        }
    }

    private class PoolWorker extends Thread {
        private boolean stopped = false;

        public void run() {
            Runnable task;

            while (!stopped) {
                synchronized (queue) {
                    //if the queue is empty
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            System.err.println("Interrupted exception in thread pool");
                            System.err.println(e.getMessage());
                        }
                    }
                    task = queue.poll();
                }

                try {
                    task.run();
                } catch (RuntimeException e) {
                    System.err.println("Runtime exception in thread pool");
                    System.err.println(e.getMessage());
                }
            }
        }

        public void stopWorker() {
            stopped = true;
        }
    }
}
// }