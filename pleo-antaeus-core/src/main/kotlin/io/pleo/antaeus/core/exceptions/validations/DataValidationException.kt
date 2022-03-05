package io.pleo.antaeus.core.exceptions.validations

open class DataValidationException (id: String, message:String, errorCode:String) : Exception("$errorCode : $message : '$id' payment process failed."){


}