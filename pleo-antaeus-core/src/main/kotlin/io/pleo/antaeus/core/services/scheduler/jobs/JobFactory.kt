package io.pleo.antaeus.core.services.scheduler.jobs

import io.pleo.antaeus.core.exceptions.validations.DataValidationException
import io.pleo.antaeus.core.utility.ErrorConstants.NOT_IMPLEMENTED_CODE
import io.pleo.antaeus.core.utility.ErrorConstants.NOT_IMPLEMENTED_MESSAGE
import io.pleo.antaeus.core.utility.Utility.JOB_TYPE_SCHEDULED

/*Factory Design Pattern
*
* Currently only gives back the Scheduled Quartz Job implementation
*
* Future: Can also implement simple trigger jobs etc
*
* */
class JobFactory {

    fun getJob( jobType: String):Job{

        when (jobType) {
            JOB_TYPE_SCHEDULED -> return ScheduledJob()

            }

        throw  DataValidationException("",NOT_IMPLEMENTED_CODE, NOT_IMPLEMENTED_MESSAGE)

    }


}