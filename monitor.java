import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

 /**
 * @(#)ThreadPool.java
 *
 * @authors
 * by Dinah Alshibi
 * @version 1.00 2023/6/4
 * The monitor class represents a thread that monitors
 * the status of the threads in the thread pool managed by the
 * ThreadPool class. It checks if any of the threads have finished
 * running and replaces them with new threads if needed.
 */

public class monitor extends Thread {
    private Thread thread = null;
    private boolean done = false; // flag to indicate if the thread is done
    private Queue<RequestInfo> buffer = new LinkedList<RequestInfo>(); // the queue for the buffer
    Semaphore poolSemaphore; // this semaphore is used to block the threads when the buffer is empty
	Semaphore bufferSemaphore; // this semaphore is used to block the threads when the buffer is full
    Semaphore mutex; // this semaphore is used to make sure that only one thread is accessing the buffer at a time
	ServeWebRequest server; // socket for the connection
    List<PoolSingleThread> unloader;
    int poolSize;
    
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
        this.thread = Thread.currentThread();
        while(!done) {
            for(int i = 0; i < poolSize; i++) {
                PoolSingleThread oldThread = unloader.get(i);
                if(oldThread.isAlive())
                    continue;
                else {
                    System.out.print("Thread is dead" + (i+1) + "\n");
                    PoolSingleThread newThread = new PoolSingleThread(buffer, poolSemaphore, bufferSemaphore,mutex, server);
                    unloader.set(i, newThread);
                    new Thread(newThread).start();
                }
            }
        }
    }

    public synchronized void doStop(){
        done = true;
        //break pool thread out of dequeue() call.
        this.thread.interrupt();
    }

}