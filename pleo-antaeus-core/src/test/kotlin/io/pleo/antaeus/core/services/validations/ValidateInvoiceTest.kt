package io.pleo.antaeus.core.services.validations

import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class ValidateInvoiceTest {


    @Test
    fun `invalid when amount is negative`() {
        Assertions.assertEquals(false,ValidateInvoice().validate(Invoice(1,1, Money(BigDecimal(-1), Currency.USD),"US",InvoiceStatus.PENDING, Date())).isValid)
    }

    @Test
    fun `invalid Country passed`() {
        Assertions.assertEquals(false,ValidateInvoice().validate(Invoice(1,1, Money(BigDecimal(50), Currency.USD),"werrr",InvoiceStatus.PENDING, Date())).isValid)
    }

    @Test
    fun `Valid data passed`() {
        Assertions.assertEquals(true,ValidateInvoice().validate(Invoice(1,1, Money(BigDecimal(50),  Currency.USD),"US",InvoiceStatus.PENDING, Date())).isValid)
    }
}
