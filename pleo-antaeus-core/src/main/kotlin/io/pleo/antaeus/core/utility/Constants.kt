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
    const val INVALID_CUSTOMER = "ERR_INVALID_CUSTOMER"
    const val INVALID_CUSTOMER_MESSAGE = "This customer does not exist"
    const val INVALID_CURRENCY_CODE = "ERR_INVALID_CURRENCY_CODE"
    const val INVALID_CURRENCY_CODE_MSSAGE = "Invalid currency code. This currency code is currently not supported"
    const val INVALID_CURRENCY_MISMATCH = "ERR_INVALID_CURRENCY_MISMATCH_CODE"
    const val INVALID_CURRENCY_MISMATCH_MSSAGE = "Currency mismatch in invoice and customer iformation"
    const val VALID = "VALID"




    //Request Validation
    const val REQUEST_INVALID_CANNOT_BE_NULL = "Request cannot be null. Certain fields cannot be null"
    const val REQUEST_INVALID_PASS_MAX_VALUES_ID= "Pass only 10 invoices to process at a time"
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