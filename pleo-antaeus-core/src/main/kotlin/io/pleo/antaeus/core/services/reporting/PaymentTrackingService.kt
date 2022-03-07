/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services.reporting

import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.PaymentTrackingRequest
import io.pleo.antaeus.models.external.PaymentResponse
import io.pleo.antaeus.models.response.PaymentTrackingResponse
import java.util.stream.Collectors

class PaymentTrackingService(private val dal: AntaeusDal) {


    fun trackPayment(paymentResponse: PaymentResponse):PaymentResponse{
        dal.insertPaymentTrackingData(paymentResponse)
        return paymentResponse
    }

    fun fetchByFilter(request: PaymentTrackingRequest):List<PaymentTrackingResponse>{
        return dal.fetchPaymentsByFilter(request)
    }

    fun fetchAll():List<PaymentResponse>{
        return dal.fetchAllPayments()
    }


    /**
     * #Serious
     * Track the status of each [paymentResponses]
     *
     * #ROFL
     * Reporting is key! Fines are a big no! Every response must be tracked perfectoollyyyy
     */
     fun trackPayment(paymentResponses: List<PaymentResponse>): List<PaymentResponse> {

        return paymentResponses.stream().map { data -> trackPayment(data) }.collect(Collectors.toList<PaymentResponse>())
    }

}
