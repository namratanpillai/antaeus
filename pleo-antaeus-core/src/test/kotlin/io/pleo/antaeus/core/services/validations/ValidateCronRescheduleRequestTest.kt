package io.pleo.antaeus.core.services.validations

import io.pleo.antaeus.core.services.validations.request.ValidateCronRescheduleRequest
import io.pleo.antaeus.models.*
import io.pleo.antaeus.models.Currency
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class ValidateCronRescheduleRequestTest {


    @Test
    fun `Valid data passed`() {
        Assertions.assertEquals(true,ValidateCronRescheduleRequest().validate(CronRequest("PaymentProcessesor","SCHEDULED","0 53 14 06 * ?","DK","DKK")).isValid)
    }

    @Test
    fun `Invalid countryCode passed`() {
        Assertions.assertEquals(false,ValidateCronRescheduleRequest().validate(CronRequest("PaymentProcessesor","SCHEDULED","0 53 14 06 * ?","test","DKK")).isValid)
    }


    @Test
    fun `Invalid currencyCode passed`() {
        Assertions.assertEquals(false,ValidateCronRescheduleRequest().validate(CronRequest("PaymentProcessesor","SCHEDULED","0 53 14 06 * ?","DK","test")).isValid)
    }

    @Test
    fun `Invalid scehdule passed`() {
        Assertions.assertEquals(false,ValidateCronRescheduleRequest().validate(CronRequest("PaymentProcessesor","SCHEDULED","0 53 ","DK","DKK")).isValid)
    }

    @Test
    fun `Invalid job type passed`() {
        Assertions.assertEquals(false,ValidateCronRescheduleRequest().validate(CronRequest("PaymentProcessesor","test","0 53 14 06 * ?","DK","DKK")).isValid)
    }


}
