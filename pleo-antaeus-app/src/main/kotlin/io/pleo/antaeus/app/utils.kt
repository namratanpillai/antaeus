
import io.pleo.antaeus.core.utility.Utility.countryCurrencyMap
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import java.math.BigDecimal
import kotlin.random.Random

/**
 * Create schemas and setup initial data for invoices, customers and payment schedules
 */
internal fun setupInitialData(dal: AntaeusDal) {


    val customers = (1..100).mapNotNull {
        var currency=Currency.values()[Random.nextInt(0, Currency.values().size)]
        dal.createCustomer(
                currency =currency , countryCode =countryCurrencyMap.get(currency)?.country.toString()
        )
    }

    customers.forEach { customer ->
        (1..10).forEach {
            dal.createInvoice(
                amount = Money(
                    value = BigDecimal(Random.nextDouble(10.0, 500.0)),
                    currency = customer.currency
                ),
                customer = customer,
                status = if (it == 1) InvoiceStatus.PENDING else InvoiceStatus.PAID,
                paymentProcessingDate = System.currentTimeMillis()
            )
        }
    }


    //Country Curency Job scheduler Entries
    dal.createCronJobs("PaymentProcessor","Payments","SCHEDULED","0 34 14 01 * ?","DK","DKK")
    dal.createCronJobs("PaymentProcessor","Payments","SCHEDULED","0 31 13 26 * ?","US","USD")
}


