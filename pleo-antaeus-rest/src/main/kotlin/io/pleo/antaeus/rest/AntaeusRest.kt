/*
    Configures the rest app along with basic exception handling and URL endpoints.
 */

package io.pleo.antaeus.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.pleo.antaeus.core.exceptions.EntityNotFoundException
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.CronJobService
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.core.services.reporting.PaymentTrackingService
import io.pleo.antaeus.core.utility.ErrorConstants.REQUEST_INVALID_CANNOT_BE_NULL
import io.pleo.antaeus.core.utility.ErrorConstants.REQUEST_INVALID_PASS_MAX_VALUES_ID
import io.pleo.antaeus.models.CronJob
import io.pleo.antaeus.models.InvoiceRequest
import io.pleo.antaeus.models.PaymentTrackingRequest
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private val thisFile: () -> Unit = {}

class AntaeusRest(
    private val invoiceService: InvoiceService,
    private val customerService: CustomerService,
    private val paymentTrackingService: PaymentTrackingService,
    private val cronJobService: CronJobService,
    private val billingService: BillingService
) : Runnable {

    override fun run() {
        app.start(7001)
    }

    // Set up Javalin rest app
    private val app = Javalin
        .create()
        .apply {
            // InvoiceNotFoundException: return 404 HTTP status code
            exception(EntityNotFoundException::class.java) { _, ctx ->
                ctx.status(404)
            }
            // Unexpected exception: return HTTP 500
            exception(Exception::class.java) { e, _ ->
                logger.error(e) { "Internal server error" }
            }
            // On 404: return message
            error(404) { ctx -> ctx.json("not found") }
        }

    init {
        // Set up URL endpoints for the rest app
        app.routes {
            get("/") {
                it.result("Welcome to Antaeus! see AntaeusRest class for routes")
            }
            path("rest") {
                // Route to check whether the app is running
                // URL: /rest/health
                get("health") {
                    it.json("ok")
                }

                // V1
                path("v1") {
                    path("invoices") {
                        // URL: /rest/v1/invoices
                        get {
                            it.json(invoiceService.fetchAll())
                        }

                        // URL: /rest/v1/invoices/{:id}
                        get(":id") {
                            it.json(invoiceService.fetch(it.pathParam("id").toInt()))
                        }

                        post("/rerun") {
                            ctx ->

                            val invoiceRequest = ctx.bodyValidator<InvoiceRequest>()
                                    .check({ it.ids != null }, REQUEST_INVALID_CANNOT_BE_NULL)
                                    .check({ it.ids!!.size <=10 }, REQUEST_INVALID_PASS_MAX_VALUES_ID)
                                    .get()
                            val response=billingService.adhocBilling(invoiceRequest.ids!!)
                            ctx.json(response)
                            ctx.status(200)
                        }
                    }

                    path("customers") {
                        // URL: /rest/v1/customers
                        get {
                            it.json(customerService.fetchAll())
                        }

                        // URL: /rest/v1/customers/{:id}
                        get(":id") {
                            it.json(customerService.fetch(it.pathParam("id").toInt()))
                        }
                    }

                    path("reporting") {
                        // URL: /rest/v1/track
                        get {
                            it.json(paymentTrackingService.fetchAll())
                        }

                        post("/filter") {
                            ctx ->
                            val mapper = jacksonObjectMapper()
                            var request:PaymentTrackingRequest=mapper.readValue(ctx.body())
                            var response=paymentTrackingService.fetchByFilter(request)
                            ctx.json(response)
                        }
                    }
                    path("job") {
                        // URL: /rest/v1/track
                        post("/reschedule") {
                            ctx ->
                            val mapper = jacksonObjectMapper()
                            var cronJob:CronJob=mapper.readValue(ctx.body())
                            cronJobService.rescheduleJob(cronJob)
                            ctx.status(200)
                        }
                    }
                }
            }
        }
    }
}
