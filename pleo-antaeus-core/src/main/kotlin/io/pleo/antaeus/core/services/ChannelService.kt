package io.pleo.antaeus.core.services

import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.external.PaymentResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.util.*

/**
* #Serious
* Opens a channel to push invoices to process
* Launches coroutines to process them in the background
*/
class ChannelService(
        private val billingService: BillingService
) {

    private val logger = KotlinLogging.logger {}

    fun pushInvoiceForProcessing(invoices: List<Invoice>):List<PaymentResponse>  = runBlocking {
        val responses=async { processInvoices(invoices,UUID.randomUUID()) }
        responses.await()

    }


    private fun processInvoices(invoices:List<Invoice>,uuid: UUID): List<PaymentResponse> {

        logger.info { "async-$uuid has started" }
        var response= mutableListOf< PaymentResponse>()

        response.addAll(billingService.billCustomer(invoices))

        return response;

    }
}


