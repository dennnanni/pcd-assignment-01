package pcd.ass01;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {

    private final BoidsModel model;
    private final SimulationStateMonitor stateMonitor;
    private final SyncWorkersMonitor coordinatorMonitor;
    private final CyclicBarrier barrier;

    private final Boid boid;

    public Worker(Boid boid,
                  BoidsModel model,
                  SimulationStateMonitor stateMonitor,
                  CyclicBarrier barrier,
                  SyncWorkersMonitor coordinatorMonitor){
        this.boid = boid;
        this.model = model;
        this.barrier = barrier;
        this.stateMonitor = stateMonitor;
        this.coordinatorMonitor = coordinatorMonitor;
    }

    @Override
    public void run() {
        while (true) {
            //System.out.println("Sono nel mio bel thread");
            stateMonitor.waitIfPaused();
            boid.updateVelocity(model);

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {}

            boid.updatePos(model);
            coordinatorMonitor.workDoneWaitCoordinator();
        }

    }
}

