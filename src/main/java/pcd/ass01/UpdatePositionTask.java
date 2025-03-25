package pcd.ass01;

import java.util.concurrent.CountDownLatch;

public class UpdatePositionTask implements Runnable {

    private final CountDownLatch latch;
    private final Boid boid;
    private final BoidsModel model;

    public UpdatePositionTask(Boid boid, BoidsModel model, CountDownLatch latch) {
        this.boid = boid;
        this.model = model;
        this.latch = latch;
    }

    @Override
    public void run() {
        boid.updatePos(model);
        latch.countDown();
    }
}
