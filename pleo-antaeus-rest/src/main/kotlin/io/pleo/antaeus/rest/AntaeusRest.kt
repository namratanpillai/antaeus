/*
    Configures the rest app along with basic exception handling and URL endpoints.
 */

package io.pleo.antaeus.rest


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.pleo.antaeus.core.exceptions.EntityNotFoundException
import io.pleo.antaeus.core.exceptions.validations.CronJobExecutionException
import io.pleo.antaeus.core.services.AdhocPaymentService
import io.pleo.antaeus.core.services.CronJobService
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.core.services.reporting.PaymentTrackingService
import io.pleo.antaeus.core.services.validations.request.ValidateCronRescheduleRequest
import io.pleo.antaeus.core.services.validations.request.ValidatePaymentTrackingRequest
import io.pleo.antaeus.core.utility.AntaeusUtil
import io.pleo.antaeus.core.utility.ErrorConstants.INVALID_JOB_DETAILS_MESSAGE
import io.pleo.antaeus.core.utility.ErrorConstants.JOB_RESCHEDULED_SUCCESS
import io.pleo.antaeus.core.utility.ErrorConstants.REQUEST_INVALID
import io.pleo.antaeus.core.utility.ErrorConstants.REQUEST_INVALID_CANNOT_BE_NULL_MESSAGE
import io.pleo.antaeus.core.utility.ErrorConstants.REQUEST_INVALID_PASS_MAX_VALUES_MESSAGE
import io.pleo.antaeus.core.utility.ErrorConstants.SUCCESS
import io.pleo.antaeus.models.CronRequest
import io.pleo.antaeus.models.InvoiceRequest
import io.pleo.antaeus.models.PaymentTrackingRequest
import io.pleo.antaeus.models.response.Response
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private val thisFile: () -> Unit = {}

class AntaeusRest(
        private val invoiceService: InvoiceService,
        private val customerService: CustomerService,
        private val paymentTrackingService: PaymentTrackingService,
        private val cronJobService: CronJobService,
        private val adhocPaymentService: AdhocPaymentService
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
                    }

                    path("invoices/id") {
                        // URL: /rest/v1/invoices/{:id}
                        get(":id") {
                            it.json(invoiceService.fetch(it.pathParam("id").toInt()))
                        }
                    }

                    path("invoices/country") {
                        get(":countryCode") {
                            val response = invoiceService.fetch(it.pathParam("countryCode"))
                            logger.info { "Number of invoices for ${response.size}" }
                            it.json(response)
                        }
                    }

                    post("invoices/rerun/ids") {
                        ctx ->

                        val invoiceRequestValidator= ctx.bodyValidator<InvoiceRequest>()
                                .check({ it.ids != null },REQUEST_INVALID_CANNOT_BE_NULL_MESSAGE)
                                .check({ it.ids!!.distinct().size <=10 }, REQUEST_INVALID_PASS_MAX_VALUES_MESSAGE)

                        try{
                            val response=adhocPaymentService.adhocBilling(invoiceRequestValidator.get().ids!!)
                            ctx.json(response)
                            ctx.status(200)
                        }catch (e:java.lang.Exception){
                            ctx.json(Response(REQUEST_INVALID,e.message!!))
                            ctx.status(400)
                        }

                    }
                    post("invoices/rerun/date") {
                        ctx ->
                        val invoiceRequestValidator= ctx.bodyValidator<InvoiceRequest>()
                                .check({ it.startDate != null },REQUEST_INVALID_CANNOT_BE_NULL_MESSAGE)
                                .check({ it.endDate != null },REQUEST_INVALID_CANNOT_BE_NULL_MESSAGE)


                        try{
                            val response=adhocPaymentService.adhocBilling(invoiceRequestValidator.get())
                            ctx.json(response)
                            ctx.status(200)
                        }catch (e:java.lang.Exception){
                            ctx.json(Response(REQUEST_INVALID,e.message!!))
                            ctx.status(400)
                        }

                    }


                path("customers") {
                    // URL: /rest/v1/customers
                    get {
                        it.json(customerService.fetchAll())
                    }

                    // URL: /rest/v1/customers/{:id}
                    get(":id") {

                        ctx->ctx.queryParam("id")
                        ctx.json(customerService.fetch(ctx.pathParam("id").toInt()))
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
                        val paymentTrackingRequest: PaymentTrackingRequest =mapper.readValue(ctx.body())
                        val validate=ValidatePaymentTrackingRequest().validate(paymentTrackingRequest)
                        if(validate.notValid()){
                            ctx.status(400)
                            ctx.json(Response(validate.errorCode,validate.errorMessage))
                            return@post
                        }
                        val response=paymentTrackingService.fetchByFilter(paymentTrackingRequest)
                        ctx.json(response)
                        ctx.status(200)
                    }
                }
                path("job") {
                    // URL: /rest/v1/job/reschedule
                    post("/reschedule") {
                        ctx ->
                        val mapper = jacksonObjectMapper()
                        val cronJobRequest: CronRequest =mapper.readValue(ctx.body())
                        val validate=ValidateCronRescheduleRequest().validate(cronJobRequest)
                        if(validate.notValid()){
                            ctx.status(400)
                            ctx.json(Response(validate.errorCode,validate.errorMessage))
                            return@post
                        }
                        try {
                            val cronJob=AntaeusUtil.convertCronRequestToCronJob(cronJobRequest)
                            cronJobService.rescheduleJob(cronJob)
                            ctx.status(200)
                            ctx.json(Response(SUCCESS,JOB_RESCHEDULED_SUCCESS+" : ${cronJob.jobClassName}"))
                        }catch (cronException:CronJobExecutionException){
                            ctx.status(400)
                            ctx.json(Response(cronException.errorCode,INVALID_JOB_DETAILS_MESSAGE))
                        }

                    }
                }
            }
        }
    }
}
}
