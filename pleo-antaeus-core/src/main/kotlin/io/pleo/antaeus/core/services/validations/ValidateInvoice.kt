package io.pleo.antaeus.core.services.validations

import io.pleo.antaeus.core.services.validations.chainofresp.ValidationResult
import io.pleo.antaeus.core.utility.ErrorConstants
import io.pleo.antaeus.models.Country
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import java.math.BigDecimal

class ValidateInvoice: Validations<Invoice> {



    override fun validate(toValidate: Invoice): ValidationResult {
        if(toValidate.amount.value.compareTo(BigDecimal.ZERO) >= 0)
            return ValidationResult(toValidate.id,false, ErrorConstants.INVALID_AMOUNT_CODE, ErrorConstants.INVALID_AMOUNT_MESSAGE)
        if(Country.values().toString().contains(toValidate.countryCode))
            return ValidationResult(toValidate.id,false, ErrorConstants.INVALID_COUNTRY_CODE, ErrorConstants.INVALID_COUNTRY_CODE_MSSAGE)
        if(Currency.values().toString().contains(toValidate.amount.currency.name))
            return ValidationResult(toValidate.id,false, ErrorConstants.INVALID_CURRENCY_CODE, ErrorConstants.INVALID_CURRENCY_CODE_MSSAGE)
        return ValidationResult(toValidate.id,false, ErrorConstants.VALID, ErrorConstants.VALID)
    }
}