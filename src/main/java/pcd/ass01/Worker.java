package pcd.ass01;

public class Worker extends Thread {

    private final BoidsModel model;
    private final SimulationStateMonitor stateMonitor;
    private final SyncWorkersMonitor coordinatorMonitor;

    public Worker(BoidsModel model, SimulationStateMonitor stateMonitor, SyncWorkersMonitor coordinatorMonitor){
        this.model = model;
        this.stateMonitor = stateMonitor;
        this.coordinatorMonitor = coordinatorMonitor;
    }

    @Override
    public void run() {
        while (true) {
            stateMonitor.waitIfPaused();
            var boids = model.getBoids();

            for (Boid boid : boids) {
                boid.updateVelocity(model);
            }

            // Barriera 1

            for (Boid boid : boids) {
                boid.updatePos(model);
            }

            coordinatorMonitor.workDoneWaitCoordinator();

        }
    }
}

