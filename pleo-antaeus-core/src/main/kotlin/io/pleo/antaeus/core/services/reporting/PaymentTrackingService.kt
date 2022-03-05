/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services.reporting

import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.PaymentTrackingRequest
import io.pleo.antaeus.models.external.PaymentResponse
import io.pleo.antaeus.models.request.PaymentTrackingResponse

class PaymentTrackingService(private val dal: AntaeusDal) {



    fun trackPayment(paymentResponse: PaymentResponse):PaymentResponse{
        dal.insertPaymentTrackingData(paymentResponse)
        return paymentResponse
    }

    fun fetchByFilter(status: PaymentTrackingRequest):List<PaymentTrackingResponse>{
        return dal.fetchPaymentsByFilter(status)
    }

    fun fetchAll():List<PaymentResponse>{
        return dal.fetchAllPayments()
    }


}
