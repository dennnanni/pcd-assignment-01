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
	

    public static void main(String[] args) {

		String input = JOptionPane.showInputDialog(null, "Insert boids count:", "Configuration", JOptionPane.QUESTION_MESSAGE);

		try {
			int boids;
			if (input == null) {
				boids = N_BOIDS;
			} else {
				boids = Integer.parseInt(input);
			}

			SimulationStateMonitor stateMonitor = new SimulationStateMonitor(false);
			SyncBoidsAgents syncMonitor = new SyncBoidsAgents(1);


			var model = new BoidsModel(
					boids,
					SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT,
					ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
					MAX_SPEED,
					PERCEPTION_RADIUS,
					AVOID_RADIUS);
			Agent worker = new Agent(model, stateMonitor, syncMonitor);
			var sim = new BoidsSimulator(model, stateMonitor, syncMonitor);
			var view = new BoidsView(model, stateMonitor, SCREEN_WIDTH, SCREEN_HEIGHT);
			sim.attachView(view);
			worker.start();
			sim.runSimulation();
		} catch (Exception ex) {
			System.out.println("Input error: integer required");
		}
    }
}
