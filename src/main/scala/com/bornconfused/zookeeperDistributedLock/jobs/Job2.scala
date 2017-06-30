package com.bornconfused.zookeeperDistributedLock.jobs

import com.bornconfused.zookeeperDistributedLock.utils.ZookeeperConnect
import com.typesafe.scalalogging.LazyLogging
import org.quartz.{Job, JobExecutionContext}

/**
  * Created by ranand on 5/15/2017 AD.
  */
class Job2 extends Job with LazyLogging with ZookeeperConnect {

  override def execute(jobExecutionContext: JobExecutionContext) = {
    if (lock.acquire) {
      logger.info(s"lock acquired by : ${this.getClass.getSimpleName}")
      Thread.sleep(1000)
      lock.release
    }

  }
}
