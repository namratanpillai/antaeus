/*
    This is the payment provider. It is a "mock" of an external service that you can pretend runs on another system.
    With this API you can ask customers to pay an invoice.

    This mock will succeed if the customer has enough money in their balance,
    however the documentation lays out scenarios in which paying an invoice could fail.
 */

package io.pleo.antaeus.core.external

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.external.PaymentResponse

interface PaymentProvider {
    //Original
    /*
        Charge a customer's account the amount from the invoice.

        Returns:
          `True` when the customer account was successfully charged the given amount.
          `False` when the customer account balance did not allow the charge.

        Throws:
          `CustomerNotFoundException`: when no customomer has the given id.
          `CurrencyMismatchException`: when the currency does not match the customer account.
          `NetworkException`: when a network error happens.
     */


    @Throws(CustomerNotFoundException::class, CurrencyMismatchException::class, NetworkException::class)
    /*Sending back PaymentResponse to give back different transaction status*/
    fun charge(invoice: Invoice): PaymentResponse
}
