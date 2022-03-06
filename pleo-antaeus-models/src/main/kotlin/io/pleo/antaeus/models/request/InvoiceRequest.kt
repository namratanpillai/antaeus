package io.pleo.antaeus.models

data class InvoiceRequest(
        var ids: List<Int>?,
        var startDate: Long?,
        var endDate: Long?,
        var countryCode:String?,
        var status:String?
)
