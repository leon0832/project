package com.juc;


import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

/**
 * * 死锁是指两个或多个以上的进程在执行过程中，
 * * 因争夺资源而造成一种互相等待的现象，
 * * 若无外力干涉那他们都将无法推进下去
 */

class HoldLockThread implements Runnable {


    private final String lockA;
    private final String lockB;

    public HoldLockThread(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }


    @SneakyThrows
    @Override
    public void run() {
        synchronized (lockA) {
            System.out.println(Thread.currentThread().getName() + "\t自己持有" + lockA + "\t尝试获得" + lockB);

            TimeUnit.SECONDS.sleep(2);

            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + "\t自己持有" + lockB + "\t尝试获得" + lockA);
            }
        }

    }
}

public class DeadLockDemo {

    public static void main(String[] args) {
        String lockA = "lockA";
        String lockB = "lockB";

        new Thread(new HoldLockThread(lockA, lockB), "ThreadAAA").start();
        new Thread(new HoldLockThread(lockB, lockA), "ThreadBBB").start();
    }
}
