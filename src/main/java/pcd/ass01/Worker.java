package pcd.ass01;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {

    private final BoidsModel model;
    private final SimulationStateMonitor stateMonitor;
    private final SyncWorkersMonitor coordinatorMonitor;
    private final CyclicBarrier barrier;

    private final int boidIndex;

    public Worker(int boidIndex,
                  BoidsModel model,
                  SimulationStateMonitor stateMonitor,
                  CyclicBarrier barrier,
                  SyncWorkersMonitor coordinatorMonitor){
        this.boidIndex = boidIndex;
        this.model = model;
        this.barrier = barrier;
        this.stateMonitor = stateMonitor;
        this.coordinatorMonitor = coordinatorMonitor;
    }

    @Override
    public void run() {
        while (true) {
            stateMonitor.waitIfPaused();
            var boid = model.getBoidWithIndex(boidIndex);

            boid.updateVelocity(model);

            // Barriera 1
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {

            }

            boid.updatePos(model);

            coordinatorMonitor.workDoneWaitCoordinator();
        }
    }
}

