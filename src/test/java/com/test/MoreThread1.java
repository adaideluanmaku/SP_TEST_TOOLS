package com.test;

import java.util.ArrayList;  
import java.util.List;  
import java.util.concurrent.ArrayBlockingQueue;  
import java.util.concurrent.ThreadPoolExecutor;  
import java.util.concurrent.TimeUnit;  
  
public class MoreThread1 {  
    //线程池  
    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(3, 5, 5,  
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),  
            new ThreadPoolExecutor.CallerRunsPolicy());  
    //定义一个线程，相当于父线程  
    private static Thread t;  
    //保存线程池中当前所有正在执行任务的活动线程，相当于子线程  
    private static List<Thread> activeThreads = new ArrayList<Thread>(5);  
    //根据参数b的值，决定是启动线程还是停止线程  
    public static void test(boolean start) {
    	if(start){
    		//创建父线程  
            t = new Thread() {  
                @Override  
                public void run() {  
                    //创建线程池要执行的两个任务r1和r2。这两个任务都是死循环  
                    Runnable r1 = new Thread() {  
                        @Override  
                        public void run() {  
                            Thread currentThread = Thread.currentThread();  
                            activeThreads.add(currentThread);  
                            int i = 1;  
                            while (true) {  
                                System.out.println(currentThread.getName()+"------------>"+(i++));  
                                try {  
                                    Thread.sleep(1000);  
                                } catch (InterruptedException e) {  
                                    e.printStackTrace();  
                                }  
                            }  
                        }  
                    };  
                    Runnable r2 = new Thread() {  
                        @Override  
                        public void run() {  
                            Thread currentThread = Thread.currentThread();  
                            activeThreads.add(currentThread);  
                            int i = 1;  
                            while (true) {  
                                System.out.println(currentThread.getName()+"------------>"+(i++));  
                                try {  
                                    Thread.sleep(1000);  
                                } catch (InterruptedException e) {  
                                    e.printStackTrace();  
                                }  
                            }  
                        }  
                    };  
                    //在线程池中执行两个任务r1和r2，实际上相当于在t中开启了两个子线程，而两个子线程由线程池维护而已  
                    pool.execute(r1);  
                    pool.execute(r2);  
                };  
            };  
            //启动父线程  
            t.start(); 
    	}else{
    		System.out.println("start========================================");  
            //停止父线程，这里使用了Thread类的暴力停止方法stop  
            t.stop();  
            //遍历并停止所有子线程，这里使用了Thread类的暴力停止方法stop  
            //这里一定要把子线程也停止掉，原来以为停止了父线程，子线程就会自动停止，事实证明这是错误的，必须在停止父线程的同时停止掉子线程才能彻底停止掉整个线程  
            for (int i = 0; i < activeThreads.size(); i++) {  
                Thread t = activeThreads.get(i);  
                System.out.println(t.getName());  
                t.stop();  
            }  
              
            System.out.println("stop=========================================="); 
    	}
    }  
    //测试方法  
    public static void main(String[] args) {  
        //传入false代表要启动线程执行任务  
        test(true);  
        try {  
            Thread.sleep(5000);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
        //执行任务5秒之后，传入false代表要停止线程  
        test(false);  
    }  
  
}  