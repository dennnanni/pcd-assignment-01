package pcd.ass01;

import java.util.concurrent.CountDownLatch;

public class UpdateVelocityTask implements Runnable {

    private final Boid boid;
    private final CountDownLatch latch;
    private final BoidsModel model;

    public UpdateVelocityTask(Boid boid,
                              BoidsModel model,
                              CountDownLatch latch) {
        this.boid = boid;
        this.model = model;
        this.latch = latch;
    }


    @Override
    public void run() {
        boid.updateVelocity(model);
        latch.countDown();
    }
}
