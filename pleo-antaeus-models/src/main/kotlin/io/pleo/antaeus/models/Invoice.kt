package io.pleo.antaeus.models

data class Invoice(
    val id: Int,
    val customerId: Int,
    val amount: Money,
    val countryCode: String,
    val status: InvoiceStatus,
    val paymentProcessingDate: Long
)
