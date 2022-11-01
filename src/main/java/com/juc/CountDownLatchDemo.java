package com.juc;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(6);

        for (int i = 1; i < 7; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "国\t灭亡");
                latch.countDown();
            }, Objects.requireNonNull(CountryEnum.getCountry(i)).getMsg()).start();
        }
        // await阻塞指导count数为0执行主线程
        latch.await();
        System.out.println("\n秦灭六国，君临天下");
    }
}
