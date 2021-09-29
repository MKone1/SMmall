package com.yxl.smmall.search.thread;

import java.util.concurrent.*;

public class ThreadTest {

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);
//创建一个线程池，形成阻

    /**
     * TODO:详解阅读源码
     *
     * @param args
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main........start");
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);

        }, executorService);

        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 0;
            System.out.println("运行结果" + i);
            return i;
        }, executorService).whenComplete((result,throwable)->{
            //可以感应异常，但是没法修改返回数据
            System.out.println("异步任务成功完成"+result+"异常时"+throwable);
        }).exceptionally(throwable -> {
            //感知异常，同时返回数据
            return 100;
        });
//        System.out.println("main .......end");
//        Integer integer = integerCompletableFuture.get();
//        System.out.println("integer"+integer);
//        CompletableFuture<Integer> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executorService).handle((result, throwable) -> {
//            if (result != null) {
//                return result*2;
//            }
//            if (throwable != null) {
//                System.out.println(throwable);
//                return 0;
//            }
//            return 0;
//        });
//        System.out.println("main .......end");
//        Integer integer = voidCompletableFuture.get();
//        System.out.println("integer" + integer);

/**线程串行化：
 * 1，thenrun:不能活儿u上一步的执行结果,但是没有返回值
 * .thenRunAsync(() -> {
 *             System.out.println("任务2启动了。。。。。。。。。。");
 *         }, executorService);
 *  2,thenAcceptAsync:能接收上一步的结果，但是没有返回值
 *.thenAcceptAsync((C)->{
 *             System.out.println("打印结果"+C);
 *         },executorService);
 *  3, thenApplyAsync :即能接收上一步的结果，还能有返回值
 *.thenApplyAsync(res->{
 *             System.out.println("打印结果"+res);
 *             return res+"";
 *         },executorService);
 */
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executorService);
//        CompletableFuture<Integer> future02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 5;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executorService);
//        不能获取返回值
//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("任务三开始");
//        },executorService);
//        可以获取返回值
//        future01.thenAcceptBothAsync(future02,(f1,f2)->{
//            System.out.println("任务三开始"+f1+"///"+f2);
//        },executorService);
        //不仅仅能获取返回值，还能进行处理，单独返回值
//        CompletableFuture<Integer> future = future01.thenCombineAsy88nc(future02, (f1, f2) -> {
//            return f1 + f2;
//        }, executorService);
//        System.out.println(future.get());


//        CompletableFuture<String> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executorService).thenApplyAsync(res->{
//            System.out.println("打印结果"+res);
//            return res+"";
//        },executorService);
//        System.out.println("main .......end");
//        String s = voidCompletableFuture.get();
//        System.out.println("返回值"+s);
        /**
         * future中有一个方法Get可以在线程执行结束后获取结果，是一个阻塞等待
         * 不仅仅可以接收一个Callable，还可以接收一个RunnAble，并且可以传递对象，
         */
        //FutureTask<Integer> futureTask = new FutureTask<Integer>((Callable<Integer>) Callable01);

        // D:线程池：给线程池直接提交任务

        //   new Thread(()-> System.out.println("hello")).start();


    }

    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
            return i;
        }
    }
} 
