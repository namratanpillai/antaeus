package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.services.scheduler.jobs.JobFactory
import io.pleo.antaeus.core.utility.Utility.JOB_PACKAGE
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.CronJob


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
