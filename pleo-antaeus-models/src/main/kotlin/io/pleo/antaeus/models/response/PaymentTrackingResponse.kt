package io.pleo.antaeus.models.response

import io.pleo.antaeus.models.Money


data class PaymentTrackingResponse(
        val id: Int,
        val customerId: Int,
        val paymentDate:Long,
        val responseCode: String,
        val responseMessage: String,
        val amount: Money,
        val countryCode: String
)
