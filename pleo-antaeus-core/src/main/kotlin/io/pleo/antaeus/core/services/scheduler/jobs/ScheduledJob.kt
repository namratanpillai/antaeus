package io.pleo.antaeus.core.services.scheduler.jobs

import io.pleo.antaeus.core.utility.Utility.countryCurrencyMap
import io.pleo.antaeus.models.CronJob
import io.pleo.antaeus.models.Currency
import org.quartz.*
import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.impl.StdSchedulerFactory
import java.util.*


class ScheduledJob: Job{

    /**
     * Creates a new job taking the class name as reference in [jobName].
     */
    override fun createJob(jobName: String): JobDetail {


        var jobClassName=Class.forName(jobName).kotlin.java
        return JobBuilder.newJob(jobClassName as Class<out org.quartz.Job>?).build()
    }

    /**
     * Creates a trigger for each country/currency combination
     * Each trigger is uniquely identified by: JOB_CLASS_NAME+CURRENCY_CODE+COUNTRY_CODE
     * The trigger will be scheduled in the desired timezone
     */
    override fun createTriggers(cronJob: CronJob): Trigger {

       return  TriggerBuilder.newTrigger().
       withIdentity(cronJob.jobClassName+"-"+cronJob.currencyCode+"-"+cronJob.countryCode).
       withSchedule(cronSchedule(cronJob.schedule)
               .inTimeZone(TimeZone.getTimeZone(countryCurrencyMap.get(Currency.valueOf(cronJob.currencyCode!!))?.timeZone.toString()))).
       withDescription(cronJob.countryCode).
       startNow().build()
    }

    /**
     * Schedules the [job] at the desired [trigger]
     */
    override fun scheduleJob(job:JobDetail,trigger: Trigger){
        val sc=StdSchedulerFactory.getDefaultScheduler()
        sc.start()
        sc.scheduleJob(job,trigger)

    }


    /**
     * Reschedule a particular [cronJob] with a new trigger.
     */
    override fun rescheduleJob(cronJob: CronJob) {
        val sc=StdSchedulerFactory.getDefaultScheduler()
        var triggerIdentity=cronJob.jobClassName+"-"+cronJob.currencyCode+"-"+cronJob.countryCode
        val trigger = sc.getTrigger(TriggerKey.triggerKey(triggerIdentity)) as CronTrigger

        if (!sc.isShutdown() && null != trigger && !cronJob.schedule.equals(trigger.getCronExpression())) {
            sc.rescheduleJob(TriggerKey.triggerKey(triggerIdentity), createTriggers(cronJob))
        }

    }


}