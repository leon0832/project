package com.jvm;

public class HelloGC {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("**********hello GC");
        Thread.sleep(Integer.MAX_VALUE);
    }
}
