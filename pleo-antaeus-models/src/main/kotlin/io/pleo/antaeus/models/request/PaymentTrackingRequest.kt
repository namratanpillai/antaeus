package io.pleo.antaeus.models

data class PaymentTrackingRequest(
        var status: String?,
        var currency: String?,
        var countryCode: String?
)
