package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.external.ExternalPaymentProviderImpl
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.reporting.PaymentTrackingService
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import io.pleo.antaeus.models.Currency
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.util.*

internal class BillingServiceTest {
    private val paymentProvider = ExternalPaymentProviderImpl()


    private val dal = mockk<AntaeusDal> {
        every { fetchCustomer(404) } returns  Customer(1,Currency.DKK,"DK")
        every { updateStatusForInvoices(mutableListOf(InvoiceIdStatus(1,"PENDING"))) }
        every { updateStatusForInvoices(mutableListOf(1),"PENDING")  }
    }

    private val invoiceService = InvoiceService(dal = dal)
    private val paymentTrackingService = PaymentTrackingService(dal = dal)
    private val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService,paymentTrackingService=paymentTrackingService)


    @Test
    fun `chunkPaymentProcessing`() {

        var response=billingService.chunkPaymentProcessing(mutableListOf(mutableListOf(Invoice(1,1, Money(BigDecimal(-1), Currency.USD),"US",InvoiceStatus.PENDING, System.currentTimeMillis()))))
        assertEquals(response.size,1)
    }
}