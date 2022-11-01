package com.juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

// 自旋锁ABA问题解决
public class CasABADemo {

    static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);
    static AtomicStampedReference<Integer> stampedReference = new AtomicStampedReference<>(100, 1);

    public static void main(String[] args) throws InterruptedException {

        System.out.println("==============ABA问题的产生=================");
        new Thread(() -> {
            atomicReference.compareAndSet(100, 101);
            atomicReference.compareAndSet(101, 100);
        }, "T1").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 实则中途已经被修改过一次
            System.out.println(atomicReference.compareAndSet(100, 2022) + "\t" + atomicReference.get());
        }, "T2").start();
        TimeUnit.SECONDS.sleep(2);
        System.out.println("==============ABA问题解决=================");


        new Thread(() -> {
            stampedReference.compareAndSet(100, 101, 1, 2);
            System.out.println("current stamp:" + stampedReference.getStamp());
            stampedReference.compareAndSet(101, 100, 2, 3);
            System.out.println("current stamp:" + stampedReference.getStamp());
        }, "T3").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 实则中途已经被修改过一次
            // 核对版本号及版本号
            System.out.println(stampedReference.compareAndSet(100, 2022, 1, 2) + "\t" + stampedReference.getReference());
        }, "T4").start();
    }
}
