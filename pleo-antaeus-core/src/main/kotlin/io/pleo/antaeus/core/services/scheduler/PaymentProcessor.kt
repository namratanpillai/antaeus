package io.pleo.antaeus.core.services.scheduler

import io.pleo.antaeus.core.external.ExternalPaymentProviderImpl
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.ChannelService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.core.services.reporting.PaymentTrackingService
import io.pleo.antaeus.core.services.utility.DatabaseConnectionHelper
import io.pleo.antaeus.core.utility.DBConstants
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging
import org.quartz.Job
import org.quartz.JobExecutionContext
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

        val invoices = invoiceService.fetchInvoicesByStatusAndCountry(InvoiceStatus.PENDING.name, listOf(countryCode),System.currentTimeMillis())

        if(invoices.isEmpty()){
            logger.info { "NO PENDING Payments to process" }
        }

        val paymentProvider = ExternalPaymentProviderImpl()
        val billingService = BillingService(paymentProvider = paymentProvider,invoiceService = invoiceService,paymentTrackingService = paymentTrackingService)

        ChannelService(billingService).pushInvoiceForProcessing(invoices)


    }
}


