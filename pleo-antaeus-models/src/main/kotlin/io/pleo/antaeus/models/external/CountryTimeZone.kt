package io.pleo.antaeus.models.external

import io.pleo.antaeus.models.Country

/**
 * Country to Timezone mapping
 * Utilized for setting the timezone in the scheduled job
 */
enum class CountryTimeZone(val country: Country, val timeZone: String){
    FR(Country.FR,"Europe/Paris"),
    US(Country.US,"America/Los_Angeles"),
    DK(Country.DK,"Europe/Copenhagen"),
    SE(Country.SE,"Europe/Stockholm"),
    UK(Country.UK,"Europe/London");



}


