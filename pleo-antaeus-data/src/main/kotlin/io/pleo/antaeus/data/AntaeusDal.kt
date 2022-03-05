/*
    Implements the data access layer (DAL).
    The data access layer generates and executes requests to the database.

    See the `mappings` module for the conversions between database rows and Kotlin objects.
 */

package io.pleo.antaeus.data

import io.pleo.antaeus.models.*
import io.pleo.antaeus.models.external.PaymentResponse
import io.pleo.antaeus.models.request.PaymentTrackingResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class AntaeusDal(private val db: Database) {
    fun fetchInvoice(id: Int): Invoice? {

        // transaction(db) runs the intfetchInvoicesByStatusAndCountryernal query as a new database transaction.
        return transaction(db) {
            // Returns the first invoice with matching id.
            InvoiceTable
                    .select { InvoiceTable.id.eq(id) }
                    .firstOrNull()
                    ?.toInvoice()
        }
    }

    fun fetchInvoice(ids: List<Int>): List<Invoice>? {

        // transaction(db) runs the intfetchInvoicesByStatusAndCountryernal query as a new database transaction.
        return transaction(db) {
            InvoiceTable.select{InvoiceTable.id.inList(ids)}
                    .map { it.toInvoice() }

        }
    }

    fun fetchInvoices(): List<Invoice> {
        return transaction(db) {
            InvoiceTable
                    .selectAll()
                    .map { it.toInvoice() }
        }
    }

    fun fetchInvoicesByStatusAndCountry(status: String, country: List<String>,processingDate:Long): List<Invoice> {
        return transaction(db) {
            InvoiceTable
                    .select { InvoiceTable.status.eq(status) and InvoiceTable.countryCode.inList(country) and InvoiceTable.paymentProcessingDate.lessEq(processingDate) }
                    .map { it.toInvoice() }
        }
    }


    fun updateStatusForInvoices(ids: List<Int>, status: String) {
        return transaction(db) {

            InvoiceTable
                    .update({ InvoiceTable.id.inList(ids) }) { row ->
                        row[InvoiceTable.status] = status
                    }
        }
    }

    fun updateStatusForInvoices(ids: List<InvoiceIdStatus>) {
        return transaction(db) {

            ids.forEach { d ->
                InvoiceTable
                        .update({ InvoiceTable.id.eq(d.id) }) { row ->
                            row[status] = d.status
                        }
            }
        }
    }

    fun createInvoice(amount: Money, customer: Customer, status: InvoiceStatus = InvoiceStatus.PENDING,paymentProcessingDate:Long): Invoice? {
        val id = transaction(db) {
            // Insert the invoice and returns its new id.
            InvoiceTable
                    .insert {
                        it[this.value] = amount.value
                        it[this.currency] = amount.currency.toString()
                        it[this.countryCode] = customer.countryCode
                        it[this.status] = status.toString()
                        it[this.customerId] = customer.id
                        it[this.paymentProcessingDate]=paymentProcessingDate
                    } get InvoiceTable.id
        }

        return fetchInvoice(id)
    }

    fun fetchCustomer(id: Int): Customer? {
        return transaction(db) {
            CustomerTable
                    .select { CustomerTable.id.eq(id) }
                    .firstOrNull()
                    ?.toCustomer()
        }
    }

    fun fetchCustomers(): List<Customer> {
        return transaction(db) {
            CustomerTable
                    .selectAll()
                    .map { it.toCustomer() }
        }
    }

    fun createCustomer(currency: Currency, countryCode: String): Customer? {
        val id = transaction(db) {
            // Insert the customer and return its new id.
            CustomerTable.insert {
                it[this.currency] = currency.toString()
                it[this.countryCode] = countryCode
            } get CustomerTable.id
        }

        return fetchCustomer(id)
    }


    fun insertPaymentTrackingData(paymentResponse: PaymentResponse) {
        transaction(db) {
            // Insert the invoice and returns its new id.
            PaymentTrackingTable
                    .insert {

                        it[this.id] = paymentResponse.id
                        it[this.customerId] = paymentResponse.customerId
                        it[this.paymentDate] = paymentResponse.paymentDate
                        it[this.responseCode] = paymentResponse.responseCode
                        it[this.responseMessage] = paymentResponse.responseMessage
                    }
        }
    }


    fun fetchCronJobs(): List<CronJob> {
        return transaction(db) {
            CronJobs
                    .selectAll()
                    .map { it.toCronJob() }
        }
    }


    fun createCronJobs(jobClassName: String, jobName: String, jobType: String, schedule: String, countryCode: String, currencyCode: String) {
        transaction(db) {
            // Insert the invoice and returns its new id.
            CronJobs
                    .insert {

                        it[this.jobClassName] = jobClassName
                        it[this.jobName] = jobName
                        it[this.jobType] = jobType
                        it[this.schedule] = schedule
                        it[this.countryCode] = countryCode
                        it[this.currencyCode] = currencyCode
                    }
        }
    }




    fun fetchAllPayments(): List<PaymentResponse> {
        return transaction(db) {
            PaymentTrackingTable
                    .selectAll()
                    .map { it.toPaymentResponse() }
        }
    }


    fun fetchPaymentsByFilter(request: PaymentTrackingRequest): List<PaymentTrackingResponse> {
        return transaction(db) {
            PaymentTrackingTable.join(InvoiceTable,JoinType.INNER,PaymentTrackingTable.id,InvoiceTable.id)
                    .select { PaymentTrackingTable.responseMessage.eq(request.status!!) or InvoiceTable.countryCode.eq(request.countryCode!!) or InvoiceTable.currency.eq(request.currency!!) }.map { it.toPaymentTrackingResponse() }
        }
    }
}


