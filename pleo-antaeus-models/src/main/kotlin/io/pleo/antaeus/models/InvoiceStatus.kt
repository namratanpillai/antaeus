package io.pleo.antaeus.models

/**
 * Status of Invoice
 * PENDING --> Payment pending to be made
 * PAID --> Payment went through successfully
 * FAILED --> Payment failed
 * PROCESSING --> Payment being worked on
 * FAILED_INVALID_DATA--> Invalid invoice data
 */
enum class InvoiceStatus {
    PENDING,
    PAID,
    FAILED,
    PROCESSING,
    FAILED_INVALID_DATA;

    companion object {
        fun getAllStatus(): List<String> {
            return values().map {
                it.toString()
            }
        }
    }
}
