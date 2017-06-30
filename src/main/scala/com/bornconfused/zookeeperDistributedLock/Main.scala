package com.bornconfused.zookeeperDistributedLock

import com.bornconfused.zookeeperDistributedLock.jobs.{Job1, Job2}
import org.quartz.{CronScheduleBuilder, JobBuilder, Trigger, TriggerBuilder}
import org.quartz.impl.StdSchedulerFactory

/**
  * Created by ranand on 6/29/2017 AD.
  */
object Main {
  def main(args: Array[String]): Unit = {

    cronSchedule
  }

  private def cronSchedule = {

    lazy val scheduler = new StdSchedulerFactory().getScheduler

    val job1 = JobBuilder
      .newJob(classOf[Job1])
      .withIdentity("Job1", "Group1")
      .build

    val job2 = JobBuilder
      .newJob(classOf[Job2])
      .withIdentity("Job2", "Group2")
      .build

    val trigger1: Trigger = TriggerBuilder.newTrigger
      .withIdentity("Trigger1", "Group1")
      .startNow()
      .withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ?"))
      .build
    val trigger2: Trigger = TriggerBuilder.newTrigger
      .withIdentity("Trigger2", "Group2")
      .startNow()
      .withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ?"))
      .build

    scheduler.start
    scheduler.scheduleJob(job1, trigger1)
    scheduler.scheduleJob(job2, trigger2)

  }
}
