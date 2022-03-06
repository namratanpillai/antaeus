package io.pleo.antaeus.core.services.validations

import io.pleo.antaeus.core.services.validations.chainofresp.ValidationResult
import io.pleo.antaeus.core.utility.ErrorConstants
import io.pleo.antaeus.models.Country
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import java.math.BigDecimal

class ValidateInvoice: Validations<Invoice> {



    override fun validate(value: Invoice): ValidationResult {
        if(value.amount.value.compareTo(BigDecimal.ZERO) < 0)
            return ValidationResult(value.id.toString(),false, ErrorConstants.INVALID_AMOUNT_CODE, ErrorConstants.INVALID_AMOUNT_MESSAGE)
        if(!Country.getCountries().contains(value.countryCode))
            return ValidationResult(value.id.toString(),false, ErrorConstants.INVALID_COUNTRY_CODE, ErrorConstants.INVALID_COUNTRY_CODE_MSSAGE)
        if(!Currency.getCurrencies().contains(value.amount.currency.name))
            return ValidationResult(value.id.toString(),false, ErrorConstants.INVALID_CURRENCY_CODE, ErrorConstants.INVALID_CURRENCY_CODE_MSSAGE)
        return ValidationResult(value.id.toString(),true, ErrorConstants.VALID, ErrorConstants.VALID)
    }
}