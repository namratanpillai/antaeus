package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.reporting.PaymentTrackingService
import io.pleo.antaeus.core.services.validations.CompositePaymentProcessingValidationRules
import io.pleo.antaeus.core.services.validations.ValidateCustomer
import io.pleo.antaeus.core.services.validations.ValidateInvoice
import io.pleo.antaeus.core.services.validations.Validations
import io.pleo.antaeus.core.services.validations.chainofresp.ValidationResult
import io.pleo.antaeus.core.utility.AntaeusUtil
import io.pleo.antaeus.core.utility.ErrorConstants
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceIdStatus
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.external.PaymentResponse
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService,
    private val paymentTrackingService:PaymentTrackingService
) {

    /**
     * #Serious:
     * Performs the following steps:
     *      Validates the list of [invoices] (Uses Composite Design Pattern)
     *      Sets the status of the  [invoices] to PROCESSING
     *      Opens an asynchronous group of connections to the External Payment Provider
     *      Post receiving all responses back update the payment tracking table.
     *      Updates the status of each [invoices]
     *
     *
     */
    fun billCustomer(invoices: List<Invoice>): List<PaymentResponse> {

        validate(invoices)

        invoiceService.updateStatusForInvoices(invoices.map { it.id }, InvoiceStatus.PROCESSING.name)
        val response = callPaymentProvider(invoices)
        CompletableFuture.allOf(*response.toTypedArray())
        val paymentResponses = trackPaymentStatus(response)
        invoiceService.updateStatusForInvoices(paymentResponses.stream().map { r -> AntaeusUtil.convertPaymentResponseToInvoiceIdStatus(r) }.collect(Collectors.toList<InvoiceIdStatus>()))
        return paymentResponses

    }




    private fun callPaymentProvider(invoices: List<Invoice>): List<CompletableFuture<PaymentResponse>> {
        return invoices.stream().map { i ->  try {
            performPayment(i)
        } catch (e:Exception){
             CompletableFuture.completedFuture(PaymentResponse(i.id,i.customerId,i.paymentProcessingDate.toString(),"Failed to perform payments",InvoiceStatus.FAILED.name))
        } }.collect(Collectors.toList<CompletableFuture<PaymentResponse>>())
    }

    private fun performPayment(invoice: Invoice): CompletableFuture<PaymentResponse> {

        return CompletableFuture.completedFuture(paymentProvider.charge(invoice))

    }

    /**
     * #Serious
     * Track the status of each [paymentResponses]
     *
     * #ROFL
     * Reporting is key! Fines are a big no! Every response must be tracked perfectoollyyyy
     */
    private fun trackPaymentStatus(paymentResponses: List<CompletableFuture<PaymentResponse>>): List<PaymentResponse> {

        return paymentResponses.stream().map { i -> i.get() }.map { data -> paymentTrackingService.trackPayment(data) }.collect(Collectors.toList<PaymentResponse>())
    }


    /**
     * #Serious
     * Composite Design Pattern to perform validations!
     *
     * Things to check!
     *      Amount should not be negative. We dont want PLEO paying for their product do we:p
     *      We support the country, currency!
     *      We support the country curency combination! (Here I made this a map, which makes it limited but we can
     *      store this in a table and cache it etc)
     *      The customer exists
     *
     */
    private fun validate(invoices: List<Invoice>) {
        val validate = mutableListOf<Validations<Invoice>>().apply {
            add(ValidateCustomer())
            add(ValidateInvoice())
        }

        val result=invoices.stream().map { i -> CompositePaymentProcessingValidationRules(validate).validate(i) }.collect(Collectors.toList<ValidationResult>()).map {  Integer.parseInt(it.id )}

        invoiceService.updateStatusForInvoices( result, InvoiceStatus.FAILED_INVALID_DATA.toString())
        result.stream().map { r->paymentTrackingService.trackPayment(PaymentResponse(r,0,"",InvoiceStatus.FAILED_INVALID_DATA.name,InvoiceStatus.FAILED_INVALID_DATA.name)) }

    }
}
