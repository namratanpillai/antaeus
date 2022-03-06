package io.pleo.antaeus.core.services.validations

import io.pleo.antaeus.core.services.validations.chainofresp.ValidationResult
import io.pleo.antaeus.core.utility.ErrorConstants
import io.pleo.antaeus.models.Invoice

 class CompositePaymentProcessingValidationRules(var validations: List<Validations<Invoice>>) : Validations<Invoice> {


     override fun validate(value: Invoice): ValidationResult {

         validations.forEach{
            r->
            run {
                val validationResult = r.validate(value)
                if (validationResult.notValid()) {
                    return validationResult
                }
            }
        }

         return ValidationResult(value.id.toString(),true, ErrorConstants.VALID, ErrorConstants.VALID)

     }


 }