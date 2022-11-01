package com.juc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousQueueDemo {
    public static void main(String[] args) {
        BlockingQueue<String> queue = new SynchronousQueue<>();

        new Thread(() -> {
            try {
                queue.put("a");
                System.out.println(Thread.currentThread().getName() + "\t put a");
                queue.put("b");
                System.out.println(Thread.currentThread().getName() + "\t put b");
                queue.put("c");
                System.out.println(Thread.currentThread().getName() + "\t put c");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "AAA").start();


        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                queue.take();
                System.out.println(Thread.currentThread().getName() + "\t take a");
                TimeUnit.SECONDS.sleep(2);
                queue.take();
                System.out.println(Thread.currentThread().getName() + "\t take b");
                TimeUnit.SECONDS.sleep(2);
                queue.take();
                System.out.println(Thread.currentThread().getName() + "\t take c");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "BBB").start();
    }
}
