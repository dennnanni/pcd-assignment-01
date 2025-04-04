package pcd.ass01.virtualthreads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SyncWorkersMonitor {
    private final int totalWorkers;
    private int finishedCount = 0;
    private ReentrantLock lock;
    private Condition sync;

    public SyncWorkersMonitor(int totalWorkers) {
        this.totalWorkers = totalWorkers;
        this.lock = new ReentrantLock();
        this.sync = lock.newCondition();
    }

    public void workDoneWaitCoordinator() {
        lock.lock();
        try {
            finishedCount++;
            if (finishedCount == totalWorkers) {
                sync.signalAll();
            }

            while (finishedCount != 0) {
                sync.await();
            }
        } catch (InterruptedException e) {
        } finally {
            lock.unlock();
        }
    }

    public void waitWorkers() {
        lock.lock();
        try {
            while (finishedCount < totalWorkers) {
                sync.await();
            }
        } catch (InterruptedException e) {
        } finally {
            lock.unlock();
        }
    }

    public void coordinatorDone() {
        lock.lock();
        finishedCount = 0;
        sync.signalAll();
        lock.unlock();
    }
}
