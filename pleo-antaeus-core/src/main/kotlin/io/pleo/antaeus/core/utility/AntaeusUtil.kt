package io.pleo.antaeus.core.utility

import io.pleo.antaeus.models.CronJob
import io.pleo.antaeus.models.CronRequest
import io.pleo.antaeus.models.InvoiceIdStatus
import io.pleo.antaeus.models.external.PaymentResponse

class AntaeusUtil {

    companion object{
        fun convertPaymentResponseToInvoiceIdStatus(paymentResponse: PaymentResponse):InvoiceIdStatus{

            return InvoiceIdStatus(paymentResponse.invoiceId,paymentResponse.responseCode)
        }

        fun convertCronRequestToCronJob(cronRequest: CronRequest):CronJob{

            return CronJob(null,cronRequest.jobClassName,null,cronRequest.jobType,cronRequest.schedule,cronRequest.countryCode,cronRequest.currencyCode)
        }

    }
}