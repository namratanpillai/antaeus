/*
    Defines the main() entry point of the app.
    Configures the database and sets up the REST web service.
 */

@file:JvmName("AntaeusApp")

package io.pleo.antaeus.app

import io.pleo.antaeus.core.external.ExternalPaymentProviderImpl
import io.pleo.antaeus.core.services.*
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
    val customerServiceDb = databaseConnectorHelper.getDb(DBConstants.URL_ANTAEUS, DBConstants.DRIVER, DBConstants.USER, DBConstants.PASSWORD)
    val cronJobServiceDb = databaseConnectorHelper.getDb(DBConstants.URL_ANTAEUS, DBConstants.DRIVER, DBConstants.USER, DBConstants.PASSWORD)
    val paymentTrackingServiceDb=databaseConnectorHelper.getDb(DBConstants.URL_ANTAEUS, DBConstants.DRIVER, DBConstants.USER, DBConstants.PASSWORD)
    // Set up data access layer.
    val dal = AntaeusDal(db = db)
    val customerServiceDal = AntaeusDal(db = customerServiceDb)
    val cronJobServiceDal = AntaeusDal(db = cronJobServiceDb)
    val paymentTrackingServiceDal = AntaeusDal(db = paymentTrackingServiceDb)

    // Insert example data in the database.
    setupInitialData(dal = dal)


    // Create core services
    val invoiceService = InvoiceService(dal = dal)
    val customerService = CustomerService(dal = customerServiceDal)
    val cronJobService = CronJobService(dal = cronJobServiceDal)
    val paymentTrackingService = PaymentTrackingService(dal = paymentTrackingServiceDal)
    val paymentProvider = ExternalPaymentProviderImpl()
    val billingService=BillingService(paymentProvider,invoiceService,paymentTrackingService)
    val adhocPaymentService=AdhocPaymentService(billingService, invoiceService)



    //Initialise the scheduled/simple jobs
    cronJobService.scheduleJobs()



    // Create REST web service
    AntaeusRest(
            invoiceService = invoiceService,
            customerService = customerService,
            paymentTrackingService=paymentTrackingService,
            cronJobService = cronJobService,
            adhocPaymentService=adhocPaymentService
    ).run()
}
