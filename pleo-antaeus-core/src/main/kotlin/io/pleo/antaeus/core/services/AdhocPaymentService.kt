package io.pleo.antaeus.core.services

import io.pleo.antaeus.models.InvoiceRequest
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.external.PaymentResponse
import java.util.stream.Collectors

class AdhocPaymentService(
    private val billingService: BillingService,
    private val invoiceService: InvoiceService
) {

    /**
     * #Serious:
     * Pass a list of [invoices] that needs to to paid ADHOC.
     * Use this in case payments failure or if you need a particular payment. This looks past the payment processing date.
     */
    fun adhocBilling(invoices: List<Int>):List<PaymentResponse>{

        val invoicesToProcess=invoiceService.fetch(invoices.distinct()).filter { i-> mutableListOf(InvoiceStatus.FAILED,InvoiceStatus.PENDING).contains(i.status) }
        return billingService.billCustomer(invoicesToProcess)

    }


    /**
     * #Serious:
     * Pass a list of invoices that needs to to paid ADHOC.
     * Use this in case payments based on country, date range and status
     */
    fun adhocBilling(request: InvoiceRequest):List<PaymentResponse>{

        val  statusList= mutableListOf(request.status!!)
        if(statusList.isEmpty()){
            statusList.addAll(mutableListOf(InvoiceStatus.FAILED.name,InvoiceStatus.PENDING.name))
        }

        val invoicesToProcess=invoiceService.fetch(request.startDate!!,request.endDate!!,request.countryCode!!).filter { i-> statusList.contains(i.status.toString()) }
        val chunkedInvoices= invoicesToProcess.chunked(10)

        chunkedInvoices.parallelStream().map { i-> ChannelService(billingService).pushInvoiceForProcessing(i)}.collect(Collectors.toList<Unit>())
        return billingService.billCustomer(invoicesToProcess)

    }
}
