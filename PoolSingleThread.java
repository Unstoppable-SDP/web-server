import java.util.Queue;
import java.util.concurrent.Semaphore;


 /**
 * @(#)PoolSingleThread.java
 *
 * @authors
 * by Hind Alrashid, Marwah Bakoor, and Dinah Alshibi
 * @version 1.00 2023/6/4
 * The PoolSingleThread class represents an individual thread 
 * in the thread pool managed by the ThreadPool class.
 * Each thread is responsible for dequeuing requests from 
 * the buffer and serving them using the ServeWebRequest class.
 * The class implements the Runnable interface and provides methods 
 * for starting, stopping, and checking the status of the thread.
 */



public class PoolSingleThread  implements Runnable  {

    private Thread thread = null;
    private Queue<RequestInfo> taskBuffer;
    private boolean done = false; // flag to indicate if the thread is done
    private Semaphore poolSemaphore;
    private Semaphore bufferSemaphore;
    private ServeWebRequest server;
    private Semaphore mutex;

    public PoolSingleThread (Queue<RequestInfo> taskBuffer, Semaphore poolSemaphore , Semaphore bufferSemaphore,Semaphore mutex, ServeWebRequest s) {
        this.taskBuffer = taskBuffer;
        this.poolSemaphore = poolSemaphore;
        this.bufferSemaphore = bufferSemaphore;
        this.server = s;
        this.mutex = mutex;
    }

    public void run() {
        this.thread = Thread.currentThread();
        // System.out.println("Thread Created " + thread.getName());
        try{
            while(!done){
                poolSemaphore.acquire();
                System.out.println("Thread " + thread.getName() + " is running");
                mutex.acquire();
                RequestInfo info =taskBuffer.poll();
                bufferSemaphore.release();
                mutex.release();           
                Thread.sleep(10000);
                server.serve(info.getSocket(), info.getQueueCount());
             }
            }  catch(Exception e){
            //log or otherwise report exception,
            //but keep pool thread alive.
            System.out.println(e);
        }

    }

    // method for checking the life of the threads 
    public boolean isAlive() {
        return thread.isAlive();
    }
    
    public synchronized void doStop(){
        done = true;
        //break pool thread out of dequeue() call.
        this.thread.interrupt();
    }

    public synchronized boolean isStopped(){
        return done;
    }

    
}
