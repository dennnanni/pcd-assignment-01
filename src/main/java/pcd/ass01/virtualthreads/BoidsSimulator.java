package pcd.ass01.virtualthreads;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoidsSimulator {

    private final SimulationStateMonitor stateMonitor;
    private SyncWorkersMonitor workersMonitor;
    private final BoidsModel model;
    private Optional<BoidsView> view;
    
    private static final int FRAMERATE = 25;
    private int framerate;
    private final List<Thread> workers;

    public BoidsSimulator(BoidsModel model, SimulationStateMonitor stateMonitor) {
        this.model = model;
        view = Optional.empty();
        this.stateMonitor = stateMonitor;
        this.workers = new ArrayList<>();
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }
      
    public void runSimulation() {
    	while (true) {
            try {
                this.stateMonitor.waitIfPausedOrStopped();
                if (workers.isEmpty()) {
                    createThreads(model.getBoids().size());
                }
            } catch (InterruptedException ex) {}

            var t0 = System.currentTimeMillis();

            workersMonitor.waitWorkers();

            model.makeCopy();

    		if (view.isPresent()) {
            	view.get().update(framerate);
            	var t1 = System.currentTimeMillis();
                var dtElapsed = t1 - t0;
                var framratePeriod = 1000/FRAMERATE;
                
                if (dtElapsed < framratePeriod) {		
                	try {
                		Thread.sleep(framratePeriod - dtElapsed);
                	} catch (Exception ex) {}
                	framerate = FRAMERATE;
                } else {
                	framerate = (int) (1000/dtElapsed);
                }
    		}

            if (stateMonitor.isStopped()) {
                interruptThreads();
            }

            workersMonitor.coordinatorDone();
    	}
    }

    private void interruptThreads() {
        for (Thread t : workers) {
            t.interrupt();
        }
        workers.clear();
    }

    private void createThreads(int nThreads) {
        workersMonitor = new SyncWorkersMonitor(nThreads);
        Barrier barrier = new Barrier(nThreads);
        var boids = model.getBoids();
		for (Boid b : boids) {
           workers.add(Thread.startVirtualThread(new Worker(b, model, stateMonitor, barrier, workersMonitor)));
        }
    }
}
