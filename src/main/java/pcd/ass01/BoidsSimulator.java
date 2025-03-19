package pcd.ass01;

import java.util.Optional;

public class BoidsSimulator {

    private final SimulationStateMonitor stateMonitor;
    private final SyncBoidsAgents workersMonitor;
    private BoidsModel model;
    private Optional<BoidsView> view;
    
    private static final int FRAMERATE = 25;
    private int framerate;
    
    public BoidsSimulator(BoidsModel model, SimulationStateMonitor stateMonitor, SyncBoidsAgents workersMonitor) {
        this.model = model;
        view = Optional.empty();
        this.stateMonitor = stateMonitor;
        this.workersMonitor = workersMonitor;

        /*
        Qui magari mettiamo la creazione degli agenti che si muovono?
         */
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }
      
    public void runSimulation() {
    	while (true) {
            stateMonitor.waitIfPaused();
            var t0 = System.currentTimeMillis();

            workersMonitor.waitWorkers();

            // Qui ci deve essere un meccanismo di sincronizzazione perché tutti i boid devono
            // aver finito gli aggiornamenti prima di poter disegnare l'interfaccia e capire se
            // bisogna aspettare per iniziare il ciclo successivo.
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
