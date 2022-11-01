package com.juc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者消费者  阻塞队列版
 * 使用：volatile、CAS、atomicInteger、BlockQueue、线程交互、原子引用
 */
public class ProdConsumerBlockQueueDemo {
    public static void main(String[] args) {
        // 传入具体的实现类， ArrayBlockingQueue
        MyResource myResource = new MyResource(new ArrayBlockingQueue<String>(10));

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t 生产线程启动");
            System.out.println();
            System.out.println();
            try {
                myResource.myProd();
                System.out.println();
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "prod").start();


        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t 消费线程启动");

            try {
                myResource.myConsumer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "consumer").start();

        // 5秒后，停止生产和消费
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println();
        System.out.println("5秒中后，生产和消费线程停止，线程结束");
        myResource.stop();
    }
}

class MyResource {
    // 默认开启，进行生产消费
    // 这里用到了volatile是为了保持数据的可见性，也就是当flag修改时，要马上通知其它线程进行修改
    private volatile boolean flag = true;

    // 使用原子包装类，而不用number++
    private final AtomicInteger atomic = new AtomicInteger();

    // 这里不能为了满足条件，而实例化一个具体的SynchronousBlockingQueue
    BlockingQueue<String> blockingQueue;

    // 而应该采用依赖注入里面的，构造注入方法传入
    public MyResource(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println(blockingQueue.getClass().getName());
    }

    public void myProd() throws Exception {
        String data;
        boolean rtValue;
        // 多线程环境的判断，一定要使用while进行，防止出现虚假唤醒
        // 当flag为true的时候，开始生产
        while (flag) {
            // 类似++i
            data = atomic.incrementAndGet() + "";
            rtValue = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if (rtValue) {
                System.out.println(Thread.currentThread().getName() + "\t插入数据成功：" + data);
            } else {
                System.out.println(Thread.currentThread().getName() + "\t插入数据失败：" + data);
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + "\t 主线程叫停 stop");
    }

    public void myConsumer() throws Exception {
        String result;
        while (flag) {
            // 类似++i

            result = blockingQueue.poll(2L, TimeUnit.SECONDS);
            if (result == null || "".equals(result)) {
                System.out.println(Thread.currentThread().getName() + "\t超过2s未消费导数据");
                flag = false;
                System.out.println();
                System.out.println();
                return;
            } else {
                System.out.println(Thread.currentThread().getName() + "\t消费到数据：" + result);
            }
        }
    }

    public void stop() {
        this.flag = false;
    }
}