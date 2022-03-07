package io.pleo.antaeus.models.external


data class PaymentResponse(
    val id: Int,
    val customerId: Int,
    val paymentDate:Long,
    val responseCode: String,
    val responseMessage: String
)
