package io.pleo.antaeus.models

data class CronJob(
    var id: Int?,
    var jobClassName:String?,
    var jobName:String?,
    var jobType: String?,
    var schedule: String?,
    var countryCode : String?,
    var currencyCode: String?
)
