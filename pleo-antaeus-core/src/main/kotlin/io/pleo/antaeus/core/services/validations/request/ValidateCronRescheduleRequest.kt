package io.pleo.antaeus.core.services.validations.request

import io.pleo.antaeus.core.services.validations.Validations
import io.pleo.antaeus.core.services.validations.chainofresp.ValidationResult
import io.pleo.antaeus.core.utility.ErrorConstants
import io.pleo.antaeus.models.*

class ValidateCronRescheduleRequest: Validations<CronRequest> {



    override fun validate(value: CronRequest): ValidationResult {
        if(!JobType.getJobTypes().contains(value.jobType.toString()))
            return ValidationResult(value.jobType.toString(),false, ErrorConstants.INVALID_JOB_TYPE, ErrorConstants.INVALID_JOB_TYPE_MESSAGE)
        if(!Country.getCountries().contains(value.countryCode.toString()))
            return ValidationResult(value.countryCode.toString(),false, ErrorConstants.INVALID_COUNTRY_CODE, ErrorConstants.INVALID_COUNTRY_CODE_MSSAGE)
        if(!Currency.getCurrencies().contains(value.currencyCode.toString()))
            return ValidationResult(value.currencyCode.toString(),false, ErrorConstants.INVALID_CURRENCY_CODE, ErrorConstants.INVALID_CURRENCY_CODE_MSSAGE)
        if(value.jobType.equals(JobType.SCHEDULED.name) && value.schedule?.split(" ")?.size != 6) {
            return ValidationResult(value.schedule.toString(),false, ErrorConstants.INVALID_SCHEDULE, ErrorConstants.INVALID_SCHEDULE_MESSAGE)
        }


        return ValidationResult("",true, ErrorConstants.VALID, ErrorConstants.VALID)
    }
}