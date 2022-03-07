package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.validations.DataValidationException
import io.pleo.antaeus.core.utility.ErrorConstants
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
    @Throws(DataValidationException::class)
    fun adhocBilling(request: InvoiceRequest):List<PaymentResponse>{

        val statusList=validateAllowedStatus(request.status!!)
        val invoicesToProcess=invoiceService.fetch(request.startDate!!,request.endDate!!,request.countryCode!!).filter { i-> statusList.contains(i.status.toString()) }
        val chunkedInvoices= invoicesToProcess.chunked(10)

        chunkedInvoices.parallelStream().map { i-> ChannelService(billingService).pushInvoiceForProcessing(i)}.collect(Collectors.toList<Unit>())
        return billingService.billCustomer(invoicesToProcess)

    }

    @Throws(DataValidationException::class)
    private fun validateAllowedStatus(status:String):List<String>{

        val allowedStatus=mutableListOf(InvoiceStatus.FAILED.name,InvoiceStatus.PENDING.name)
        val  statusList= mutableListOf<String>()

        if(status.isNotEmpty()){
            if(!allowedStatus.contains(status)){
                throw DataValidationException("",ErrorConstants.INVALID_STATUS,ErrorConstants.INVALID_STATUS_MESSAGE)
            }else{
                statusList.add(status)
            }
        }else{
            statusList.addAll(allowedStatus)
        }
        return statusList
    }
}
