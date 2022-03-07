/*
    Defines mappings between database rows and Kotlin objects.
    To be used by `AntaeusDal`.
 */

package io.pleo.antaeus.data

import io.pleo.antaeus.models.*
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.external.CountryTimeZone
import io.pleo.antaeus.models.external.PaymentResponse
import io.pleo.antaeus.models.response.PaymentTrackingResponse
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

var countryCurrencyMap= mapOf(Currency.DKK to CountryTimeZone.DK, Currency.USD to CountryTimeZone.US, Currency.EUR to CountryTimeZone.FR
        , Currency.SEK to CountryTimeZone.SE, Currency.GBP to CountryTimeZone.UK)

fun ResultRow.toInvoice(): Invoice = Invoice(
    id = this[InvoiceTable.id],
    amount = Money(
        value = this[InvoiceTable.value],
        currency = Currency.valueOf(this[InvoiceTable.currency])
    ),
    status = InvoiceStatus.valueOf(this[InvoiceTable.status]),
    customerId = this[InvoiceTable.customerId],
        countryCode = countryCurrencyMap.get(Currency.valueOf(this[InvoiceTable.currency]))?.country.toString(),
        paymentProcessingDate = this[InvoiceTable.paymentProcessingDate]
)

fun ResultRow.toCustomer(): Customer = Customer(
    id = this[CustomerTable.id],
    currency = Currency.valueOf(this[CustomerTable.currency]),
        countryCode = countryCurrencyMap.get(Currency.valueOf(this[CustomerTable.currency]))?.country.toString()
)

fun ResultRow.toCronJob(): CronJob = CronJob(
        id = this[CronJobs.id],
        jobClassName=this[CronJobs.jobClassName],
        jobName = this[CronJobs.jobName],
        jobType=this[CronJobs.jobType],
        schedule=this[CronJobs.schedule],
        countryCode = this[CronJobs.countryCode],
        currencyCode = this[CronJobs.currencyCode]
)

fun ResultRow.toPaymentResponse(): PaymentResponse = PaymentResponse(
        id = this[PaymentTrackingTable.id],
        customerId=this[PaymentTrackingTable.customerId],
        paymentDate = this[PaymentTrackingTable.paymentDate],
        responseCode=this[PaymentTrackingTable.responseCode],
        responseMessage=this[PaymentTrackingTable.responseMessage]
)

fun ResultRow.toPaymentTrackingResponse(): PaymentTrackingResponse = PaymentTrackingResponse(
        id = this[PaymentTrackingTable.id],
        customerId = this[PaymentTrackingTable.customerId],
        paymentDate = this[PaymentTrackingTable.paymentDate],
        responseCode = this[PaymentTrackingTable.responseCode],
        responseMessage = this[PaymentTrackingTable.responseMessage],
        countryCode = this[InvoiceTable.countryCode],
        amount = Money(
                value = this[InvoiceTable.value],
                currency = Currency.valueOf(this[InvoiceTable.currency])
        )

)
