package com.lgd.base.thread;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * Describe: ForkJoin批处理分页数据
 * author: guodong.li
 * datetime: 2017/6/7 18:47
 */
public class BatchPageForkJoinMain {

    private final static int TOTAL_NUM = 512;
    private final static int PAGE_SIZE = 20;

    private final static Logger LOGGER = LoggerFactory.getLogger(BatchPageForkJoinMain.class);

    public static void main(String[] args) {

        int total_page = 0;
        if(TOTAL_NUM%PAGE_SIZE==0){
            total_page = TOTAL_NUM/PAGE_SIZE;
        } else {
            total_page = TOTAL_NUM/PAGE_SIZE + 1;
        }
        LOGGER.info("总共有{}页。",total_page);

        BatchPageForkJoinMain app = new BatchPageForkJoinMain();

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountPageNumTask task = app.new CountPageNumTask(1,total_page);
        Future<Long> result = forkJoinPool.submit(task);
        try{
            System.out.println("The fail task num is "
                    + result.get());
        } catch(Exception e){
            LOGGER.error("获取值出错："+e);
        }


    }


    private class CountPageNumTask extends RecursiveTask<Long> {
        //一个线程10个分页
        private static final int THRESHOLD = 10; //阀值

        private int start;
        private int end;

        public CountPageNumTask(int start, int end){
            this.start = start;
            this.end = end;
        }


        @Override
        protected Long compute() {

            System.out.println("Thread ID: " + Thread.currentThread().getId() +
                    ",Thread Name: " + Thread.currentThread().getName());

            //返回失败的记录数
            Long failTaskNum = 0L;

            if((end -start) <= THRESHOLD){
                failTaskNum = handleData(start,end);
            } else {
                int middle = (start + end) / 2;
                CountPageNumTask leftTask = new CountPageNumTask(start, middle);
                CountPageNumTask rightTask = new CountPageNumTask(middle + 1, end);
                leftTask.fork();
                rightTask.fork();

                Long leftFailResult = leftTask.join();
                Long rightFailResult = rightTask.join();

                failTaskNum = leftFailResult + rightFailResult;
            }
            return failTaskNum;
        }
    }

    public static Long handleData(int startPage, int endPage) {

        for (int i = startPage; i<=endPage; i++) {
//            Random random = new Random();
//            try {
//                Thread.sleep(random.nextInt(5)*1000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            LOGGER.info("当前线程名：{}.", Thread.currentThread().getName());
            LOGGER.info("第{}页正在处理第{}至{}条数据.", i,(i-1)*PAGE_SIZE, i*PAGE_SIZE-1);
        }
        //返回失败的任务数
        return 5L;
    }
}
