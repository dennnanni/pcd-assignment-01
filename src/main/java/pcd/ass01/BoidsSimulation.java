package pcd.ass01;

import javax.swing.*;

public class BoidsSimulation {

	final static int N_BOIDS = 1500;

	final static double SEPARATION_WEIGHT = 1.0;
    final static double ALIGNMENT_WEIGHT = 1.0;
    final static double COHESION_WEIGHT = 1.0;

    final static int ENVIRONMENT_WIDTH = 1000; 
	final static int ENVIRONMENT_HEIGHT = 1000;
    static final double MAX_SPEED = 4.0;
    static final double PERCEPTION_RADIUS = 50.0;
    static final double AVOID_RADIUS = 20.0;

	final static int SCREEN_WIDTH = 800; 
	final static int SCREEN_HEIGHT = 800;
	public static final int N_THREADS = Runtime.getRuntime().availableProcessors() + 1;


	public static void main(String[] args) {

        SimulationStateMonitor stateMonitor = new SimulationStateMonitor(false);
        SyncWorkersMonitor syncMonitor = new SyncWorkersMonitor(N_THREADS);

		var model = new BoidsModel(
				SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT,
				ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
				MAX_SPEED,
				PERCEPTION_RADIUS,
				AVOID_RADIUS);
		var sim = new BoidsSimulator(model, stateMonitor, syncMonitor);
		var view = new BoidsView(model, stateMonitor, SCREEN_WIDTH, SCREEN_HEIGHT);
		sim.attachView(view);

		Barrier barrier = new Barrier(N_THREADS);

		int divisionFactor = boids / N_THREADS + 1;
		for (int i = 0; i < boids; i += divisionFactor) {
			int controlledBoids = i + divisionFactor <= boids ? divisionFactor : (boids - i);
			Worker worker = new Worker(i, controlledBoids, model, stateMonitor, barrier, syncMonitor);
			worker.start();
		}

		sim.runSimulation();
    }
}
