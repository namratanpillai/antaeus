package io.pleo.antaeus.models


enum class Country {
    FR,
    US,
    DK,
    SE,
    UK;


    companion object {
        fun getCountries(): List<String> {
            return values().map {
                it.toString()
            }
        }
    }
}


