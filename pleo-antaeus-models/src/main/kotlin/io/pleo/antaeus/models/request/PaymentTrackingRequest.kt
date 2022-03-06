package io.pleo.antaeus.models

data class PaymentTrackingRequest(
        var status: String?,
        var currencyCode: String?,
        var countryCode: String?
)
