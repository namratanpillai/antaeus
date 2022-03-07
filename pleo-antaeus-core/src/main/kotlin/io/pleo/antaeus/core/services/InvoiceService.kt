/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceIdStatus

class InvoiceService(private val dal: AntaeusDal) {
    fun fetchAll(): List<Invoice> {
        return dal.fetchInvoices()
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id.toString())
    }

    fun fetch(countryCode: String): List<Invoice> {
        return dal.fetchInvoice(countryCode) ?: throw InvoiceNotFoundException(countryCode)
    }

    fun fetch(id: List<Int>): List<Invoice> {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id.joinToString (prefix = "[", postfix = "]"))
    }

    fun fetch(paymentStartDate: Long,paymentEndDate: Long,countryCode:String): List<Invoice> {
        return dal.fetchInvoice(paymentStartDate,paymentEndDate,countryCode) ?: throw InvoiceNotFoundException("")
    }


    fun fetchInvoicesByStatusAndCountry(status: String,country:List<String>,processingDate:Long): List<Invoice> {
      return dal.fetchInvoicesByStatusAndCountry(status,country,processingDate)
    }


    fun updateStatusForInvoices(ids:List<Int>, status:String){
        dal.updateStatusForInvoices(ids, status)
    }


    fun updateStatusForInvoices(ids:List<InvoiceIdStatus>){
        dal.updateStatusForInvoices(ids)
    }

}
