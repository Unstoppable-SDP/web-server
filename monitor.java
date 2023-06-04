import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class monitor {
    private final Queue<Runnable> buffer = new LinkedList<Runnable>(); // the queue for the buffer
    Semaphore poolSemaphore; // this semaphore is used to block the threads when the buffer is empty
	Semaphore bufferSemaphore; // this semaphore is used to block the threads when the buffer is full
    
    monitor(List<PoolSingleThread> unloader, Queue<Runnable> buffer, Semaphore poolSemaphore, Semaphore bufferSemaphore, int poolSize) {
        PoolSingleThread monitorThread = new PoolSingleThread(buffer, poolSemaphore, bufferSemaphore); 
        monitorThread.run();

        while(true) {
            for(int i = 0; i < poolSize; i++) {
                if(unloader.get(i).isAlive())
                    continue;
                else {
                    unloader.get(i).doStop();
                    PoolSingleThread newThread = new PoolSingleThread(buffer, poolSemaphore, bufferSemaphore);
                    unloader.add(newThread);
                    new Thread(newThread).start();
                }
            }
        }
    }

}

