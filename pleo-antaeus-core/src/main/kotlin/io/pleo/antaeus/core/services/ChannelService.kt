package io.pleo.antaeus.core.services

import io.pleo.antaeus.models.Invoice
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import mu.KotlinLogging
import java.util.*
import kotlin.system.measureTimeMillis

/**
* #Serious
* Opens a channel to push invoices to process
* Launches coroutines to process them in the background
*/
class ChannelService(
        private val billingService: BillingService
) {

    private val logger = KotlinLogging.logger {}

    fun pushInvoiceForProcessing(invoices: List<Invoice>)= runBlocking {

        val invoicesChannel= Channel<List<Invoice>>()

        GlobalScope.launch { // launch a new co-routine in background and continue
            logger.info { "Processing invoices ${invoices.size}"}
            invoicesChannel.send(invoices)
            invoicesChannel.close()
        }

        val t = measureTimeMillis {

            coroutineScope {
                val uuid=UUID.randomUUID()
                logger.info { "Launching coroutines: payment-1-$uuid and payment-2-$uuid"}
                launch (CoroutineName("payment-1-$uuid")){processInvoices(invoicesChannel)}
                launch (CoroutineName("payment-2-$uuid")){processInvoices(invoicesChannel)} }
            }
        logger.info { t }
    }




    private suspend fun processInvoices(invoicesChannel: ReceiveChannel<List<Invoice>>){

        for (o in invoicesChannel){
            billingService.billCustomer(o)
        }
    }

}


