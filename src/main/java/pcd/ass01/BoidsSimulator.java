package pcd.ass01;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BoidsSimulator {

    private static final int NTHREADS = Runtime.getRuntime().availableProcessors() + 1 ;

    private final SimulationStateMonitor monitor;
    private BoidsModel model;
    private Optional<BoidsView> view;
    private Executor executor;
    private CountDownLatch taskCounter;
    
    private static final int FRAMERATE = 25;
    private int framerate;
    
    public BoidsSimulator(BoidsModel model, SimulationStateMonitor monitor) {
        this.model = model;
        view = Optional.empty();
        this.monitor = monitor;
        this.executor = Executors.newFixedThreadPool(NTHREADS);
        this.taskCounter = new CountDownLatch(model.getBoids().size());
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }
      
    public void runSimulation() {
    	while (true) {
            this.monitor.waitIfPaused();
            var t0 = System.currentTimeMillis();
    		var boids = model.getBoids();

            for (Boid b : boids) {
                this.executor.execute(new UpdateVelocityTask(b, model, taskCounter));
            }

            try {
                taskCounter.await();
            } catch (InterruptedException ex) {

            } finally {
                taskCounter = new CountDownLatch(boids.size());
            }

            for (Boid b : boids) {
                this.executor.execute(new UpdatePositionTask(b, model, taskCounter));
            }

            try {
                taskCounter.await();
            } catch (InterruptedException ex) {

            }

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
            
    	}
    }
}
