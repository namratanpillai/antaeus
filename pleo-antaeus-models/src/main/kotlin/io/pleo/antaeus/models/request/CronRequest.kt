package io.pleo.antaeus.models

data class CronRequest(

        var jobClassName:String,
        var jobType: String?,
        var schedule: String?,
        var countryCode : String?,
        var currencyCode: String?
)
