package akka.communication

import akka.actor.{Actor, ActorSystem, Props}
import akka.rpc.RegisterWorker
import com.typesafe.config.ConfigFactory

import scala.collection.immutable.HashMap


/**
  * Created by liguodong on 2017/1/2.
  */

class Master extends Actor{

  //保存注册的worker
  val idToWorker = new HashMap[String,_]()


  println("调用主构造器")

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    println("调用preStart方法")
  }

  override def receive: Receive = {
    case RegisterWorker(id,memory,cores) => {


      println("a client connected")
      //Master 反馈给 Worker
      //回复
      sender ! "reply"
    }
    case "hello" => println("hello")
  }
}


object Master {

  def main(args: Array[String]): Unit = {

    val host = args(0)
    val port = args(1).toInt
//    val host = "127.0.0.1"
//    val port = "8888".toInt

    //准备配置
    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
       """.stripMargin
    val config = ConfigFactory.parseString(configStr)
    //ActorSystem老大，辅助创建和监控下面的Actor,单例对象
    val actorSystem = ActorSystem("MasterSystem",config)

    //创建Actor
    val master = actorSystem.actorOf(Props(new Master),"Master")

    master ! "hello"
    actorSystem.awaitTermination()
    //Await.result()
  }

}
