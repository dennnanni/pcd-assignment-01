package pcd.ass01.virtualthreads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {

    private final int workersCount;
    private int counter;
    private ReentrantLock lock;
    private Condition cond;

    public Barrier(int workersCount) {
        this.workersCount = workersCount;
        this.counter = workersCount;
        this.lock = new ReentrantLock();
        this.cond = lock.newCondition();
    }

    public void await() throws InterruptedException {
        lock.lock();
        counter--;
        if (counter > 0) {
            cond.await();
        } else {
            counter = workersCount;
            cond.signalAll();
        }

        lock.unlock();
    }
}
