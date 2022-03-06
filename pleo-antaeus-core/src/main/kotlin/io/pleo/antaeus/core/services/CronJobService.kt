package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.services.scheduler.jobs.JobFactory
import io.pleo.antaeus.core.utility.Utility.JOB_PACKAGE
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.CronJob


/**
 * Fetches all jobs to schedule from the table CronJobs. Structure of CronJobs:
        Field                   | Value
        ------------------------|-------------
        job_class_payment_name  | Name of the kotlin class to run
        job_name                | Name of the job
        job_type                | Type of job. Eg: SCHEDULED, SIMPLE.
        schedule                | The cron trigger schedule
        country_code            | The country for which the job has to be scheduled
        currency_code           | Associated currency

 * Schedules the jobs using Quartz Scheduler in specific timezones
 * Provides for rescheduling already running jobs
 */
class CronJobService(private val dal: AntaeusDal) {


    /**
     * Picks and schedules all the jobs
     */
    fun scheduleJobs(){

        var jobsToSchedule = getAllValidJobsToSchedule()

        jobsToSchedule.forEach { cronJob -> scheduleJob(cronJob) }
    }



    private fun  getAllValidJobsToSchedule():List<CronJob>{
        return  dal.fetchCronJobs()
    }


    /**
     * #Serious:
     * Factory + Template pattern Design pattern
     * Initiate the job
     */
    private fun scheduleJob(cronJob: CronJob){


        var job=JobFactory().getJob(cronJob.jobType!!)
        var jobDetails=job.createJob("""$JOB_PACKAGE${cronJob.jobClassName}""")
        var trigger=job.createTriggers(cronJob)
        job.scheduleJob(jobDetails,trigger)
    }


    /**
     * #Serious:
     * Factory + Template pattern Design pattern
     */
    fun rescheduleJob(cronJob: CronJob){
        var job=JobFactory().getJob(cronJob.jobType!!)
        job.rescheduleJob(cronJob)
    }

}
