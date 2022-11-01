package com.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一个初始值为0的变量，两个线程对其进行交替操作，一个加一 一个减一，5轮
 * * 线程 操作 资源类
 * * 判断 干活 通知
 * * 防止虚假唤醒机制
 */
public class ProdConsumerTraditionDemo {

    public static void main(String[] args) {
        ShareData shareData = new ShareData();
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    shareData.increment();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "AA").start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    shareData.decrement();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "BB").start();
    }

}

/**
 * 资源类
 */
class ShareData {
    private int number = 0;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public void increment() throws Exception {

        // 同步代码块，加锁
        lock.lock();
        // 判断
        while (number != 0) {
            // 等待不能消费
            condition.await();
        }
        // 干活
        number++;
        System.out.println(Thread.currentThread().getName() + "\t" + number);
        // 通知 唤醒
        condition.signal();
        lock.unlock();
    }

    public void decrement() throws Exception {

        // 同步代码块，加锁
        lock.lock();
        // 判断
        while (number != 1) {
            // 等待不能消费
            condition.await();
        }

        // 干活
        number--;
        System.out.println(Thread.currentThread().getName() + "\t" + number);
        // 通知 唤醒
        condition.signal();
        lock.unlock();
    }
}