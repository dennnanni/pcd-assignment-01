package pcd.ass01;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SyncBoidsAgents {

    private final Lock lock = new ReentrantLock();
    private final Condition workersDone = lock.newCondition();
    private final Condition coordinatorDone = lock.newCondition();

    private final int nWorkers;
    private int nWorkersDone;
    private boolean coordinatorWorking;

    public SyncBoidsAgents(int nWorkers) {
        this.nWorkers = nWorkers;
    }


    public void waitWorkers() {
        lock.lock();
        try {
            while (nWorkersDone < nWorkers)
                workersDone.await();

            coordinatorWorking = true;
            nWorkersDone = 0;
        } catch (InterruptedException ex){
        } finally {
            lock.unlock();
        }
    }

    public void coordinatorDone() {
        lock.lock();
        coordinatorWorking = false;
        coordinatorDone.signalAll();
        lock.unlock();
    }

    public synchronized void workDoneWaitCoordinator() {
        lock.lock();
        try {
            nWorkersDone++;
            if (nWorkersDone == nWorkers) {
                workersDone.signal();
            }

            while (coordinatorWorking) {
                coordinatorDone.await();
            }
        } catch (InterruptedException ex) {
        } finally {
            lock.unlock();
        }
    }
}
