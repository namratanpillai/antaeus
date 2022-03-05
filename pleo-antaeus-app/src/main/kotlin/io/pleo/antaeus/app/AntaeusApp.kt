/*
    Defines the main() entry point of the app.
    Configures the database and sets up the REST web service.
 */

@file:JvmName("AntaeusApp")

package io.pleo.antaeus.app

import io.pleo.antaeus.core.external.ExternalPaymentProviderImpl
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.CronJobService
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.core.services.reporting.PaymentTrackingService
import io.pleo.antaeus.core.services.utility.DatabaseConnectionHelper
import io.pleo.antaeus.core.utility.DBConstants
import io.pleo.antaeus.data.*
import io.pleo.antaeus.rest.AntaeusRest
import setupInitialData

fun main() {

    // The tables to create in the database.
    val tables = arrayOf(InvoiceTable, CustomerTable,PaymentTrackingTable,CronJobs)
    val databaseConnectorHelper = DatabaseConnectionHelper()

    val db = databaseConnectorHelper.setupInitialDB(DBConstants.URL_ANTAEUS, DBConstants.DRIVER, DBConstants.USER, DBConstants.PASSWORD,tables)

    // Set up data access layer.
    val dal = AntaeusDal(db = db)

    // Insert example data in the database.
    setupInitialData(dal = dal)


    // Create core services
    val invoiceService = InvoiceService(dal = dal)
    val customerService = CustomerService(dal = dal)
    val cronJobService = CronJobService(dal = dal)
    val paymentTrackingService = PaymentTrackingService(dal = dal)
    val paymentProvider = ExternalPaymentProviderImpl()
    val billingService=BillingService(paymentProvider,invoiceService,paymentTrackingService)



    //Initialise the scheduled/simple jobs
    cronJobService.scheduleJobs()



    // Create REST web service
    AntaeusRest(
         invoiceService = invoiceService,
         customerService = customerService,
         paymentTrackingService=paymentTrackingService,
         cronJobService = cronJobService,
         billingService=billingService
    ).run()
}
