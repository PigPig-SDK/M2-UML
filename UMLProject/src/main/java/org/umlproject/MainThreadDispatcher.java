package org.umlproject;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is a template for classes to execute code on the main thread
 * There will be a CLI and FX version of this class... Because I hate my life.
 */
public class MainThreadDispatcher {
    
    /**
     * The list of tasks to be executed.
     */
    protected final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
    /**
     * This can be FXDispatcher or MainThreadDispatcher.
     */
    public static MainThreadDispatcher dispatcher;
    public boolean shutDown = false;
    
    /**
     * Runs the queued actions
     * Only call this on the main thread, or else this is class is a waste of time
     */
    public void processQueuedActions() {
        Runnable task;
        while ((task = queue.poll()) != null) {
            
            if(shutDown) return;//Stop working on thread!
            
            try {
                task.run();
            } catch (Exception e) {
                if(!shutDown) e.printStackTrace();
            }
        }
    }
    /**
     * Hand over a job for the main thread to execute.
     */
    public void dispatch(Runnable action)
    {
        if (action != null)
            this.queue.add(action);
    }
}
