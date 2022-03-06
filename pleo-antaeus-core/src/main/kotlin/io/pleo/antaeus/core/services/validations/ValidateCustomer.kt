package io.pleo.antaeus.core.services.validations

import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.utility.DatabaseConnectionHelper
import io.pleo.antaeus.core.services.validations.chainofresp.ValidationResult
import io.pleo.antaeus.core.utility.DBConstants
import io.pleo.antaeus.core.utility.ErrorConstants
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice

class ValidateCustomer: Validations<Invoice> {


    val db = DatabaseConnectionHelper().getDb(DBConstants.URL_ANTAEUS, DBConstants.DRIVER, DBConstants.USER, DBConstants.PASSWORD)

    // Set up data access layer.
    val dal = AntaeusDal(db = db)
    val customerService = CustomerService(dal = dal)


    override fun validate(value: Invoice): ValidationResult {
        try{
            val customer=customerService.fetch(value.customerId)
            customer.let {

                if(!customer.currency.equals(value.amount.currency)){
                    return ValidationResult(value.id.toString(),true, ErrorConstants.INVALID_CURRENCY_MISMATCH, ErrorConstants.INVALID_CURRENCY_MISMATCH_MSSAGE)
                }

                return ValidationResult(value.id.toString(),true, ErrorConstants.VALID, ErrorConstants.VALID)
            }

        }catch (customerException: CustomerNotFoundException){
            return ValidationResult(value.id.toString(),true, ErrorConstants.INVALID_CUSTOMER, ErrorConstants.INVALID_CUSTOMER_MESSAGE)
        }
    }
}