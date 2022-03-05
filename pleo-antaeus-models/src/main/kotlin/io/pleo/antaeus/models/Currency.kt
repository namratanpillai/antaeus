package io.pleo.antaeus.models

enum class Currency {
    EUR,
    USD,
    DKK,
    SEK,
    GBP;



    companion object {
        fun getCurrencies(): List<String> {
            return InvoiceStatus.values().map {
                it.toString()
            }
        }
    }
}


