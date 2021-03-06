package com.lgd.scala.thread

import java.util.concurrent._

/**
  * Created by liguodong on 2017/3/5.
  */
object BlockQueueMain {

  def main(args: Array[String]): Unit = {

    val fixedThreadPool: ExecutorService = Executors.newFixedThreadPool(3)
    val latch: CountDownLatch = new CountDownLatch(11)
    val blockingQueue: BlockingQueue[Integer] = new ArrayBlockingQueue[Integer](5)

    //生产者
    fixedThreadPool.submit(new BlockQueueConsumer(latch,blockingQueue))

    //消费者
    for(i <- 1 to 10) {
      fixedThreadPool.submit(new BlockQueueProducer(latch,blockingQueue))
    }

    println("wait start...")
    latch.await()
    println("wait finish...")
    fixedThreadPool.shutdown()

  }

}
