package com.juc;

import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueueDemo {
    public static void main(String[] args) {

        // add remove 超过抛出异常
        // offer poll 返回特殊值 true/false null
        // put take 一直阻塞
        // offer(time) poll(time) 超时退出

        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(3);

        System.out.println(queue.add("a"));
        System.out.println(queue.add("b"));
        System.out.println(queue.add("c"));
        System.out.println(queue.add("d"));


        System.out.println(queue.remove());
        System.out.println(queue.remove());
        System.out.println(queue.remove());
    }
}
