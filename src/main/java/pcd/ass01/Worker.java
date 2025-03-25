package pcd.ass01;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {

    private final BoidsModel model;
    private final SimulationStateMonitor stateMonitor;
    private final SyncWorkersMonitor coordinatorMonitor;
    private final CyclicBarrier barrier;

    private final int boidIndex;
    private final int controlledBoids;

    public Worker(int boidIndex,
                  int controlledBoids,
                  BoidsModel model,
                  SimulationStateMonitor stateMonitor,
                  CyclicBarrier barrier,
                  SyncWorkersMonitor coordinatorMonitor){
        this.boidIndex = boidIndex;
        this.controlledBoids = controlledBoids;
        this.model = model;
        this.barrier = barrier;
        this.stateMonitor = stateMonitor;
        this.coordinatorMonitor = coordinatorMonitor;
    }

    @Override
    public void run() {
        while (true) {
            stateMonitor.waitIfPaused();
            var boids = model.getBoids();

            for (int i = boidIndex; i < boidIndex + controlledBoids; i++) {
                boids.get(i).updateVelocity(model);
            }

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {}

            for (int i = boidIndex; i < boidIndex + controlledBoids; i++) {
                boids.get(i).updatePos(model);
            }

            coordinatorMonitor.workDoneWaitCoordinator();
        }

    }
}

