package com.juc;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 集合类不安全问题
 * <p>
 * 【故障现象】
 * java.util.ConcurrentModificationExceptioin
 * 【解决方案】
 * 1. Vector，源码加了锁  但性能低，since:jdk1.1 （不建议）
 * 2. Collections.synchronizedList(new ArrayList());
 * 3. new CopyOnWriteArrayList(); 写时复制，读写分离思想,推荐
 * 【导致原因】
 * 并发争抢修改导致
 */
public class ContainerNotSafeDemo {

    public static void main(String[] args) {

        List<String> list = new CopyOnWriteArrayList<>();
        //List<String> list = new ArrayList<>();
        //高并发异常：java.util.ConcurrentModificationExceptioin
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0, 6));
                System.out.println(list);
            }, String.valueOf(i)).start();
        }
        list.forEach(System.out::println);
    }
}
