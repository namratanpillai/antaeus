package io.pleo.antaeus.core.services.validations

import io.pleo.antaeus.core.services.validations.chainofresp.ValidationResult

interface Validations<T> {

    fun validate(value: T): ValidationResult

}