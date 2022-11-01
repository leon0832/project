package com.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 题目：多线程之间按顺序调用，实现 A-> B -> C 三个线程启动，要求如下：
 * AA打印5次，BB打印10次，CC打印15次
 * 紧接着
 * AA打印5次，BB打印10次，CC打印15次
 * ..
 * 10轮
 */
public class SyncAndReentrantLockDemo {

    public static void main(String[] args) {
        ShareResource resource = new ShareResource();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    resource.print5();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "AA").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    resource.print10();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "BB").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    resource.print15();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "CC").start();
    }
}

class ShareResource {
    // A 1   B 2   c 3
    private int number = 1;
    // 创建一个重入锁
    private final Lock lock = new ReentrantLock();

    // 这三个相当于备用钥匙
    private final Condition c1 = lock.newCondition();
    private final Condition c2 = lock.newCondition();
    private final Condition c3 = lock.newCondition();

    public void print5() throws InterruptedException {
        lock.lock();
        while (number != 1) {
            c1.await();
        }
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + "\t :" + i);
        }
        number = 2;
        c2.signal();
        lock.unlock();
    }

    public void print10() throws InterruptedException {
        lock.lock();
        while (number != 2) {
            c2.await();
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName() + "\t :" + i);
        }
        number = 3;
        c3.signal();
        lock.unlock();
    }

    public void print15() throws InterruptedException {
        lock.lock();
        while (number != 3) {
            c3.await();
        }
        for (int i = 0; i < 15; i++) {
            System.out.println(Thread.currentThread().getName() + "\t :" + i);
        }
        number = 1;
        c1.signal();
        lock.unlock();
    }
}
