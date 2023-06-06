import java.util.Date;
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
    private boolean done = false; // flag for checking the life of the threads
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
    
            while(!done){
                try{
                poolSemaphore.acquire();
                //System.out.println("Thread " + thread.getName() + " is running");
                mutex.acquire();
                RequestInfo info =taskBuffer.poll();
                bufferSemaphore.release();
                System.out.println("Connecton "+info.getQueueCount()+" opend in "+thread.getName()+". ("+new Date()+")");
                mutex.release(); 
                server.serve(info.getSocket(), info.getQueueCount());
                //Thread.sleep(100000);
                info.getSocket().close();    
            } catch(Exception e){
                e.printStackTrace();
            }
               
             }


    }

    // method for checking the life of the threads 
    public boolean isAlive() {
        return thread.isAlive();
    }
    
    public void doStop(){
        done = true;
        //break pool thread out of dequeue() call.
        this.thread.interrupt();
    }
    
}
