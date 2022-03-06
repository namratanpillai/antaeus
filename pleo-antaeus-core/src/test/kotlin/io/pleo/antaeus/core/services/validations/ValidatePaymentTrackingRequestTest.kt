package io.pleo.antaeus.core.services.validations

import io.pleo.antaeus.core.services.validations.request.ValidatePaymentTrackingRequest
import io.pleo.antaeus.models.PaymentTrackingRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ValidatePaymentTrackingRequestTest {


    @Test
    fun `Valid data passed`() {
        Assertions.assertEquals(true,ValidatePaymentTrackingRequest().validate(PaymentTrackingRequest("PENDING","USD","US")).isValid)
    }

    @Test
    fun `Invalid status passed`() {
        Assertions.assertEquals(false,ValidatePaymentTrackingRequest().validate(PaymentTrackingRequest("TEST","USD","US")).isValid)
    }

    @Test
    fun `Invalid country code passed`() {
        Assertions.assertEquals(false,ValidatePaymentTrackingRequest().validate(PaymentTrackingRequest("PENDING","USD","test")).isValid)
    }

    @Test
    fun `Invalid currncy code passed`() {
        Assertions.assertEquals(false,ValidatePaymentTrackingRequest().validate(PaymentTrackingRequest("PENDING","test","US")).isValid)
    }
}
