package pcd.ass01;

public class Worker extends Thread {

    private final BoidsModel model;
    private final SimulationStateMonitor stateMonitor;
    private final SyncWorkersMonitor coordinatorMonitor;
    private final Barrier barrier;

    private final Boid boid;
    private boolean isRunning = true;

    public Worker(Boid boid,
                  BoidsModel model,
                  SimulationStateMonitor stateMonitor,
                  Barrier barrier,
                  SyncWorkersMonitor coordinatorMonitor){
        this.boid = boid;
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
            } catch (InterruptedException e) {
            }
            boid.updateVelocity(model);

            try {
                barrier.await();
            } catch (InterruptedException ex) {}

            boid.updatePos(model);
            coordinatorMonitor.workDoneWaitCoordinator();
        }

    }

    @Override
    public void interrupt() {
        isRunning = false;
    }
}

