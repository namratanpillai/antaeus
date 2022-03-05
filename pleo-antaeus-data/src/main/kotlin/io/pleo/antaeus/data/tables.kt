/*
    Defines database tables and their schemas.
    To be used by `AntaeusDal`.
 */

package io.pleo.antaeus.data

import org.jetbrains.exposed.sql.Table

object InvoiceTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val currency = varchar("currency", 3)
    val countryCode = varchar("country_code", 4)
    val value = decimal("value", 1000, 2)
    val customerId = reference("customer_id", CustomerTable.id)
    val status = text("status")
    val paymentProcessingDate=long("payment_processing_date")
}

object CustomerTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val currency = varchar("currency", 3)
    val countryCode=varchar("country_code", 4)
}

object PaymentTrackingTable : Table() {
    val id = integer("id")
    val customerId =integer("customer_id")
    val paymentDate= varchar("payment_date",50)
    val responseCode= varchar("response_code", 255)
    val responseMessage= varchar("response_message", 255)
}


object CronJobs : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val jobClassName = varchar("job_class_payment_name", 50)
    val jobName=varchar("job_name", 50)
    val jobType = varchar("job_type", 50)
    val schedule = varchar("schedule", 30)
    val countryCode= varchar("country_code", 5)
    val currencyCode= varchar("currency_code", 5)

}