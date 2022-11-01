package com.jvm;

/**
 * 在Java中能作为GC Roots的对象有:
 * 1. 虚拟机栈中的引用对象
 * 2. 方法区中的静态类属性引用对象
 * 3. 方法区中的常量引用对象
 * 4. 本地方法栈中的JNI中引用对象
 */
public class GCRootDemo {

    private byte[] bytes = new byte[100 * 1024 * 1024];

    // 方法区中的类静态属性引用的对象
    // private static GCRootDemo2 t2;

    // 方法区中的常量引用，GC Roots 也会以这个为起点，进行遍历
    // private static final GCRootDemo3 t3 = new GCRootDemo3(8);

    public static void m1() {
        // 第一种，虚拟机栈中的引用对象
        GCRootDemo t1 = new GCRootDemo();
        System.gc();
        System.out.println("第一次GC完成");
    }

    public static void main(String[] args) {
        m1();
    }
}
