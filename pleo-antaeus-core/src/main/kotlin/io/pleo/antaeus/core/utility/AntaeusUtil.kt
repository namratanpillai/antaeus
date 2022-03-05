package io.pleo.antaeus.core.utility

import io.pleo.antaeus.models.InvoiceIdStatus
import io.pleo.antaeus.models.external.PaymentResponse

class AntaeusUtil {

    companion object{
        fun convertPaymentResponseToInvoiceIdStatus(paymentResponse: PaymentResponse):InvoiceIdStatus{

            return InvoiceIdStatus(paymentResponse.id,paymentResponse.responseMessage)
        }
    }
}