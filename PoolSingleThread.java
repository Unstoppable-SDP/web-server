import java.util.Queue;
import java.util.concurrent.Semaphore;

public class PoolSingleThread  implements Runnable  {

    private Thread thread = null;
    private Queue<Runnable> taskBuffer;
    private boolean done = false; // flag to indicate if the thread is done
    private Semaphore poolSemaphore;
    private Semaphore bufferSemaphore;

    public PoolSingleThread (Queue<Runnable> taskBuffer, Semaphore poolSemaphore , Semaphore bufferSemaphore) {
        this.taskBuffer = taskBuffer;
        this.poolSemaphore = poolSemaphore;
        this.bufferSemaphore = bufferSemaphore;
    }

    public void run() {
        this.thread = Thread.currentThread();
        System.out.println("Thread Created " + thread.getName());
        try{
            while(!done){
                poolSemaphore.acquire();
                System.out.println("The thread start working again");
                Runnable runnable = (Runnable) taskBuffer.poll();
                bufferSemaphore.release();
                // System.out.println("The Semaphore count is " + count.availablePermits());
                runnable.run();
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
