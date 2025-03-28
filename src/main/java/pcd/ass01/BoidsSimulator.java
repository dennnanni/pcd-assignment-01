package pcd.ass01;

import java.util.Optional;

public class BoidsSimulator {

    private final SimulationStateMonitor stateMonitor;
    private final SyncWorkersMonitor workersMonitor;
    private BoidsModel model;
    private Optional<BoidsView> view;
    
    private static final int FRAMERATE = 25;
    private int framerate;
    
    public BoidsSimulator(BoidsModel model, SimulationStateMonitor stateMonitor, SyncWorkersMonitor workersMonitor) {
        this.model = model;
        view = Optional.empty();
        this.stateMonitor = stateMonitor;
        this.workersMonitor = workersMonitor;
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }
      
    public void runSimulation() {
    	while (true) {
            stateMonitor.waitIfPaused();
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

            workersMonitor.coordinatorDone();

    	}
    }
}
