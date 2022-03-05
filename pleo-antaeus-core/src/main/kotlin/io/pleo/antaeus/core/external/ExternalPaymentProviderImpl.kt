package io.pleo.antaeus.core.external

import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.external.PaymentResponse
import java.util.*


class ExternalPaymentProviderImpl:PaymentProvider {


    val responseMap= mapOf("SUCCESS" to InvoiceStatus.PAID.name,"SERVER_UNAVAILABLE" to InvoiceStatus.FAILED.name,
            "LOW_BALANCE" to InvoiceStatus.FAILED.name)

    /**
     * #Serious:
     * Mock Implementation of an external payment provider.
     * Gives back success and failure responses against each [invoice]
     *
     * SUCCESS --> When the payment went through successfully
     * SERVER_UNAVAILABLE--> In case if there was a server unavailability on the external process side
     * LOW_BALANCE --> Low balance leads to payment failure
     *
     * #ROFL
     * There can be multiple scenarios for failure, in my case most probably it'll always be LOW BALANCE ROFL!
     */
    override fun charge(invoice: Invoice): PaymentResponse {

        val randomResponse=responseMap.entries.shuffled().first()
        return PaymentResponse(invoice.id,invoice.customerId, Date().toString(),randomResponse.key, randomResponse.value)
    }
}