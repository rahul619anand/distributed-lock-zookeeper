package com.bornconfused.zookeeperDistributedLock.utils

import com.bornconfused.zookeeperDistributedLock.lock.ZookeeperDistributedLock
import com.typesafe.scalalogging.LazyLogging
import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}

/**
  * Created by ranand on 6/30/2017 AD.
  */
trait ZookeeperConnect extends LazyLogging{

  val zookeeper = new ZooKeeper("localhost:2181", 60000, new Watcher {
    override def process(event: WatchedEvent): Unit =
      logger.debug(s"Event: $event")
  })

  val distributedLock  = new ZookeeperDistributedLock(zookeeper, "/lockNode", "lock1")

  def lock = distributedLock


}
