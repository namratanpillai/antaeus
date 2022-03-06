package io.pleo.antaeus.models

enum class Currency {
    EUR,
    USD,
    DKK,
    SEK,
    GBP;



    companion object {
        fun getCurrencies(): List<String> {
            return values().map {
                it.toString()
            }
        }
    }
}


