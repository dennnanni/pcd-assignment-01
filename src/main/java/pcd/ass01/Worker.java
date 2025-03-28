package pcd.ass01;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {

    private final BoidsModel model;
    private final SimulationStateMonitor stateMonitor;
    private final SyncWorkersMonitor coordinatorMonitor;
    private final Barrier barrier;

    private final int boidIndex;
    private final int controlledBoids;

    public Worker(int boidIndex,
                  int controlledBoids,
                  BoidsModel model,
                  SimulationStateMonitor stateMonitor,
                  Barrier barrier,
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

            System.out.println("[Thread " + boidIndex + "]: arrivato alla barriera");
            try {
                barrier.await();
            } catch (InterruptedException ex) {}

            for (int i = boidIndex; i < boidIndex + controlledBoids; i++) {
                boids.get(i).updatePos(model);
            }

            System.out.println("[Thread " + boidIndex + "]: ciclo finito");
            coordinatorMonitor.workDoneWaitCoordinator();
        }

    }
}

