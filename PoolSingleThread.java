import java.util.Queue;
import java.util.concurrent.Semaphore;

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
        System.out.println("Thread Created " + thread.getName());
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
        }

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
