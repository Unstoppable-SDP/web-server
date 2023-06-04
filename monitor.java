import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class monitor extends Thread {
    private Queue<RequestInfo> buffer = new LinkedList<RequestInfo>(); // the queue for the buffer
    Semaphore poolSemaphore; // this semaphore is used to block the threads when the buffer is empty
	Semaphore bufferSemaphore; // this semaphore is used to block the threads when the buffer is full
    Semaphore mutex; // this semaphore is used to make sure that only one thread is accessing the buffer at a time
	ServeWebRequest server; // socket for the connection
    List<PoolSingleThread> unloader;
    int poolSize;
    private volatile boolean done = false;
    
    monitor(List<PoolSingleThread> unloader, Queue<RequestInfo> buffer, Semaphore poolSemaphore, Semaphore bufferSemaphore, int poolSize, Semaphore mutex, ServeWebRequest server) {
        this.unloader = unloader;
        this.buffer = buffer;
        this.poolSemaphore = poolSemaphore;
        this.bufferSemaphore = bufferSemaphore;
        this.mutex = mutex;
        this.server = server;
        this.poolSize = poolSize;
    }

    public void run() {
        while(! isInterrupted()) {
            for(int i = 0; i < poolSize; i++) {
                PoolSingleThread oldThread = unloader.get(i);
                if(oldThread.isAlive())
                    continue;
                else {
                    System.out.print("Thread is dead" + i + "\n");
                    PoolSingleThread newThread = new PoolSingleThread(buffer, poolSemaphore, bufferSemaphore,mutex, server);
                    unloader.set(i, newThread);
                    new Thread(newThread).start();
                }
            }

            try {
                Thread.sleep(1000); // wait for 1 second before checking again
            } catch (InterruptedException e) {
                // ignore the interrupted exception
            }
        }
    }

}