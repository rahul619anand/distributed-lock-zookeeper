package com.bornconfused.zookeeperDistributedLock

import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}
import org.quartz.{Job, JobExecutionContext}

/**
  * Created by ranand on 5/15/2017 AD.
  */
class LockJob extends Job with LazyLogging {

  override def execute(jobExecutionContext: JobExecutionContext) = {

    val zookeeper = new ZooKeeper("localhost:2181", 100000, new Watcher {
      override def process(event: WatchedEvent): Unit =
        logger.info(s"Watch Event: $event")
    })

    Thread.sleep(3000)
    val zookeeperDistributedLock =
      new ZookeeperDistributedLock(zookeeper, "/root", "lock1")
    val isLocked = zookeeperDistributedLock.lock

    if (isLocked) {
      logger.info("Job 1 locked")
      Thread.sleep(10000)
      zookeeperDistributedLock.unlock
    }

  }
}
