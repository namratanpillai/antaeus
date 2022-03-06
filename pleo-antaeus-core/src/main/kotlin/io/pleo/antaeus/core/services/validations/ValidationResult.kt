package io.pleo.antaeus.core.services.validations.chainofresp

class ValidationResult( var id:String, var isValid:Boolean,  var errorCode: String,  var errorMessage: String) {


    fun notValid(): Boolean {
        return !isValid
    }
}