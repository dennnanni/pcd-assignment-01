package pcd.ass01;

public class Worker extends Thread {

    private final BoidsModel model;
    private final SimulationStateMonitor stateMonitor;
    private final SyncWorkersMonitor coordinatorMonitor;
    private final Barrier barrier;

    private final int boidIndex;
    private final int controlledBoids;
    private boolean isRunning = true;

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
        while (isRunning) {
            try {
                stateMonitor.waitIfPausedOrStopped();
            } catch (InterruptedException ex) {
                break;
            }

            var boids = model.getBoids();

            for (int i = boidIndex; i < boidIndex + controlledBoids; i++) {
                boids.get(i).updateVelocity(model);
            }

            try {
                barrier.await();
            } catch (InterruptedException ex) {
                break;
            }

            for (int i = boidIndex; i < boidIndex + controlledBoids; i++) {
                boids.get(i).updatePos(model);
            }

            coordinatorMonitor.workDoneWaitCoordinator();
        }
    }

    @Override
    public void interrupt() {
        isRunning = false;
    }
}

