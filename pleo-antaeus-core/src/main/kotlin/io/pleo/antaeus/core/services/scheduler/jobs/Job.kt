package io.pleo.antaeus.core.services.scheduler.jobs

import io.pleo.antaeus.models.CronJob
import org.quartz.JobDetail
import org.quartz.Trigger


interface Job {

     fun createJob( jobName: String): JobDetail

     fun createTriggers(cronJob: CronJob):Trigger

     fun scheduleJob(job:JobDetail,trigger: Trigger)


     fun rescheduleJob(cronJob: CronJob)
}