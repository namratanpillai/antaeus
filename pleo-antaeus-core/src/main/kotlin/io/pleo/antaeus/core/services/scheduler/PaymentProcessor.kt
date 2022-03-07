package io.pleo.antaeus.core.services.scheduler

import io.pleo.antaeus.core.external.ExternalPaymentProviderImpl
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.ChannelService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.core.services.reporting.PaymentTrackingService
import io.pleo.antaeus.core.services.utility.DatabaseConnectionHelper
import io.pleo.antaeus.core.utility.AntaeusUtil
import io.pleo.antaeus.core.utility.DBConstants
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.InvoiceIdStatus
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.external.PaymentResponse
import mu.KotlinLogging
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

class PaymentProcessor(): Job {

    private val logger = KotlinLogging.logger {}

    //Get All valid invoices as per currency code
    override fun execute(context: JobExecutionContext?) {

        var countryCode=""
        if (context != null) {
            countryCode=context.trigger.description
        }else{
            logger.error { "Error initiating PaymentProcessor." }
        }

        logger.info { "Initiating invoice processing $countryCode  "}
        val dal = AntaeusDal(db = DatabaseConnectionHelper().getDb(DBConstants.URL_ANTAEUS,DBConstants.DRIVER,DBConstants.USER,DBConstants.PASSWORD))

        val invoiceService = InvoiceService(dal = dal)
        val paymentTrackingService = PaymentTrackingService(dal = dal)

        //Fetching only PENDING payments to process
        val invoices = invoiceService.fetchInvoicesByStatusAndCountry(InvoiceStatus.PENDING.name, listOf(countryCode),System.currentTimeMillis())

        if(invoices.isEmpty()){
            logger.info { "NO PENDING Payments to process" }
        }

        //Updating the chunk to PROCESSING
        invoiceService.updateStatusForInvoices(invoices.map { it.id }, InvoiceStatus.PROCESSING.name)

        val paymentProvider = ExternalPaymentProviderImpl()
        val billingService = BillingService(paymentProvider = paymentProvider,invoiceService = invoiceService,paymentTrackingService = paymentTrackingService)

        val chunkedInvoices= invoices.chunked(10)

        //Stream collect responses
        var paymentResponse=chunkedInvoices.stream().map { i-> ChannelService(billingService).pushInvoiceForProcessing(i)}.collect(Collectors.toList<List<PaymentResponse>>()).flatten()

        invoiceService.updateStatusForInvoices(paymentResponse.parallelStream().map { r -> AntaeusUtil.convertPaymentResponseToInvoiceIdStatus(r)}.collect(Collectors.toList<InvoiceIdStatus>()))

        paymentTrackingService.trackPayment(paymentResponse)


        }
}


