package io.pleo.antaeus.core.utility

import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.external.CountryTimeZone

object ErrorConstants {

    const val INVALID_AMOUNT_CODE = "ERR_INVALID_AMOUNT"
    const val INVALID_AMOUNT_MESSAGE = "Invalid amount. Ensure amount is greater than 0"
    const val NOT_IMPLEMENTED_CODE = "ERR_NOT_IMPLEMENTED"
    const val NOT_IMPLEMENTED_MESSAGE = "Functionality not implemented"
    const val INVALID_COUNTRY_CODE = "ERR_INVALID_COUNTRY_CODE"
    const val INVALID_COUNTRY_CODE_MSSAGE = "Invalid country code. This country is currently not supported"
    const val INVALID_STATUS = "INVALID_STATUS"
    const val INVALID_STATUS_MESSAGE = "Invalid status passed. This status is not supported not supported"
    const val INVALID_CUSTOMER = "ERR_INVALID_CUSTOMER"
    const val INVALID_CUSTOMER_MESSAGE = "This customer does not exist"
    const val INVALID_CURRENCY_CODE = "ERR_INVALID_CURRENCY_CODE"
    const val INVALID_CURRENCY_CODE_MSSAGE = "Invalid currency code. This currency code is currently not supported"
    const val INVALID_CURRENCY_MISMATCH = "ERR_INVALID_CURRENCY_MISMATCH_CODE"
    const val INVALID_CURRENCY_MISMATCH_MSSAGE = "Currency mismatch in invoice and customer iformation"
    const val VALID = "VALID"
    const val INVALID_JOB_DETAILS = "INVALID_JOB_DETAILS"
    const val INVALID_JOB_DETAILS_MESSAGE = "The job details passed may be invalid."
    const val INVALID_JOB_TYPE="INVALID_JOB_TYPE"
    const val INVALID_JOB_TYPE_MESSAGE="This type of cron is not supported currently"
    const val INVALID_SCHEDULE="INVALID_SCHEDULE"
    const val INVALID_SCHEDULE_MESSAGE="Schedule passed is invalid"

    const val SUCCESS="SUCCESS"
    const val JOB_RESCHEDULED_SUCCESS = "Rescheduled Job successfully"

    const val FAILED_TO_PERFORM_ACTION="FAILED_TO_PERFORM_ACTION"




    //Request Validation
    const val REQUEST_INVALID="REQUEST_INVALID"
    const val REQUEST_INVALID_CANNOT_BE_NULL_MESSAGE= "Request cannot be null. Certain fields cannot be null"
    const val REQUEST_INVALID_PASS_MAX_VALUES_MESSAGE= "Pass only 10 invoices to process at a time"
}

object DBConstants {

    const val URL_ANTAEUS = "jdbc:sqlite:antaues"
    const val DRIVER = "org.sqlite.JDBC"
    const val USER = "root"
    const val PASSWORD = ""

}

object Utility{

    var countryCurrencyMap= mapOf(Currency.DKK to CountryTimeZone.DK, Currency.USD to CountryTimeZone.US, Currency.EUR to CountryTimeZone.FR
            , Currency.SEK to CountryTimeZone.SE, Currency.GBP to CountryTimeZone.UK)

    var JOB_PACKAGE="io.pleo.antaeus.core.services.scheduler."

    var JOB_TYPE_SCHEDULED="SCHEDULED"
}