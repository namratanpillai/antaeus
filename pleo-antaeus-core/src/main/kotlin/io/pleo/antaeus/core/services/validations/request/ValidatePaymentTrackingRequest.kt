package io.pleo.antaeus.core.services.validations.request

import io.pleo.antaeus.core.services.validations.Validations
import io.pleo.antaeus.core.services.validations.chainofresp.ValidationResult
import io.pleo.antaeus.core.utility.ErrorConstants
import io.pleo.antaeus.models.Country
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.PaymentTrackingRequest

class ValidatePaymentTrackingRequest: Validations<PaymentTrackingRequest> {



    override fun validate(value: PaymentTrackingRequest): ValidationResult {
        if(value.status!!.isNotEmpty() && !InvoiceStatus.getAllStatus().contains(value.status.toString())) {
            return ValidationResult(value.status.toString(),false, ErrorConstants.INVALID_STATUS, ErrorConstants.INVALID_STATUS_MESSAGE)
        }

        if(value.countryCode!!.isNotEmpty() && !Country.getCountries().contains(value.countryCode.toString()))
            return ValidationResult(value.countryCode.toString(),false, ErrorConstants.INVALID_COUNTRY_CODE, ErrorConstants.INVALID_COUNTRY_CODE_MSSAGE)
        if(value.currencyCode!!.isNotEmpty() && !Currency.getCurrencies().contains(value.currencyCode.toString()))
            return ValidationResult(value.currencyCode.toString(),false, ErrorConstants.INVALID_CURRENCY_CODE, ErrorConstants.INVALID_CURRENCY_CODE_MSSAGE)


        return ValidationResult("",true, ErrorConstants.VALID, ErrorConstants.VALID)
    }
}