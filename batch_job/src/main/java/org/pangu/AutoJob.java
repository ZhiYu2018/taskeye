package org.pangu;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AutoJob {
    private Random random;
    public AutoJob(){
        random = new Random();
    }

    @SkyEyeTarget(app="Teana", job="SendNotify")
    public String run(String name) throws InterruptedException {
        int r = random.nextInt(10000);
        Thread.sleep(r);
        return name + String.valueOf(100/(r % 10));
    }
}
