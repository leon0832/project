package com.juc;

/**
 * volatile的应用，单例模式DCL版避免高并发下的安全问题
 */
public class SingletonDemo {

    // DCL+volatile避免多线程情况下单例模式的安全问题
    private static volatile SingletonDemo instance = null;

    private SingletonDemo() {
        System.out.println("create instance *********");
    }

    public static SingletonDemo getInstance() {
        if (instance == null) {
            // DCL(双端检索机制)+volatile避免多线程情况下单例模式的安全问题
            synchronized (SingletonDemo.class) {
                if (instance == null) {
                    instance = new SingletonDemo();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(SingletonDemo.getInstance() == SingletonDemo.getInstance());
            }, String.valueOf(i)).start();
        }
    }
}
