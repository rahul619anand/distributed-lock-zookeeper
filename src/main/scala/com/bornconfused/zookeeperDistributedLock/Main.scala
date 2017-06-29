package com.bornconfused.zookeeperDistributedLock

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

    val job = JobBuilder
      .newJob(classOf[LockJob])
      .withIdentity("Job", "Group")
      .build

    val job2 = JobBuilder
      .newJob(classOf[LockJob2])
      .withIdentity("Job2", "Group2")
      .build

    val trigger: Trigger = TriggerBuilder.newTrigger
      .withIdentity("Trigger", "Group")
      .startNow()
      .withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ?"))
      .build
    val trigger2: Trigger = TriggerBuilder.newTrigger
      .withIdentity("Trigger2", "Group2")
      .startNow()
      .withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ?"))
      .build

    scheduler.start
    scheduler.scheduleJob(job, trigger)
    scheduler.scheduleJob(job2, trigger2)

  }
}
